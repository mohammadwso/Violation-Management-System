# ADR 0002: Two ways to create violations, with asynchronous ingestion through Kafka

- **Status:** Accepted
- **Date:** 2026-11-11
- **Related:** [ADR 0001](0001-upload-violation-images-through-api.md), [ADR 0003](0003-violation-service-design.md)

## Context

Violations come from different kinds of sources, and they need different things:

| | CCTV / VIM (machines) | Inspector (person) |
|---|---|---|
| Traffic | High, in bursts | Low, steady |
| Needs | Throughput, and no lost events | An immediate answer: created, or what's wrong |

Creating a violation is slow work: validation, reference lookups (region, type, violator), moving images, and database inserts. If machine bursts write straight into the database, they can overload it and slow down inspectors too.

## Decision

There are **two ways in**, and both use the same shared create-violation logic in `ViolationService` ([ADR 0003](0003-violation-service-design.md)).

### 1. Synchronous: inspectors

- `POST /violations/images` → `{ imageKey }` (per [ADR 0001](0001-upload-violation-images-through-api.md))
- `POST /violations` → runs the create logic right away → **201 Created** with the violation, or **400** with validation errors.

### 2. Asynchronous: CCTV / VIM through Kafka

```
CCTV / VIM ──POST /violations/images──▶ API ──▶ MinIO staging/
CCTV / VIM ──POST /violations/ingest──▶ API ──▶ Kafka topic: violation.detected ──▶ 202 Accepted { eventId }
                                                       │
                                                       ▼
                                           Consumer (@KafkaListener)
                                                       │ shared logic: validate → promote images → insert
                                                       ├── success → commit offset
                                                       └── repeated failure → violation.detected.DLT
```

- **The ingest endpoint only checks the request shape and publishes.** It doesn't touch the database, so it stays fast and can scale on its own.
- **Messages carry image keys, never images** (claim-check pattern). Kafka messages should stay small (the default limit is 1 MB).
- **Topic `violation.detected`:**
  - Message key = `sourceId`. Events from one camera stay in order, and different cameras spread across partitions.
  - Partition count limits consumer parallelism (at most one consumer per partition in a group). Start with 6.
  - Retention of 7 days, so events can be replayed after a bug fix or outage.
- **Producer:** `acks=all`, with idempotence enabled (the default in current Kafka clients).
- **Errors:** Spring Kafka's `DefaultErrorHandler` retries with backoff, then `DeadLetterPublishingRecoverer` sends the event to `violation.detected.DLT` for inspection. Validation errors (bad data) go to the DLT immediately without retrying.

### Idempotency (both paths)

Kafka and HTTP retries both deliver **at least once**, so the same request can arrive twice.

**The image keys are the idempotency keys.** Every image upload (ADR 0001) gets a **server-generated, unique key**. A retried `POST /violations` or a redelivered Kafka message carries the **same image keys**, so it can be recognized without any help from the client.

- `violation_images.image_key` has a **unique constraint**. An image key can belong to only one violation.
- The create logic just inserts. A unique-constraint violation means "already processed": the HTTP path returns the existing violation, and the consumer commits the offset and skips it. No check-then-insert, because two consumers could race.
- **Same image, several violations** (e.g. speeding and no seat belt from one frame): the client uploads the image **once per violation**. Each upload gets its own key, so the violations don't collide. The cost is a duplicate copy in storage, which is acceptable.
- **Same image uploaded twice for one violation** (e.g. the client retries the upload): only one key is used. The unused upload stays in `staging/` and is deleted by the lifecycle rule (ADR 0001). This is expected.
- **Not covered:** a client that re-uploads its images *and* posts the violation again gets new keys, so it creates a second violation. Clients must retry only the step that failed.

**Alternatives rejected**
- **Client-generated `external_id`** with a unique `(source_id, external_id)`: depends on every client generating IDs correctly and reusing them on retry. We don't want correctness to depend on clients.
- **Server-computed hash of business fields** (source, date, plate, type): breaks if a retry formats a field differently, and adds a key that has to be kept in sync with the fields.
- **Unique SHA-256 of image bytes:** would block the same image from being used in more than one violation. The SHA-256 is still stored, but only for evidence integrity, without a unique constraint.

**Business duplicates are a different problem.** Detecting the **same real-world violation twice** (two cameras, one car) isn't idempotency. It's a business rule (same plate + type + region within N minutes → flag for review).

### Monitoring

- [Kafka UI](https://github.com/kafbat/kafka-ui) (the maintained `kafbat/kafka-ui` fork) in docker-compose, to browse topics and messages, inspect the DLT, and see **consumer lag** (how far the consumer is behind, the key health signal).

## Alternatives considered

- **Synchronous only:** simplest, but machine bursts hit the database directly, and an outage drops events unless every client retries well.
- **RabbitMQ:** simpler to run, with retries and DLQs built in. It's a natural fit for "do this task once". Kafka was chosen because events are **kept and can be replayed**, and **more consumers** (analytics, notifications, audit) can read the same stream independently later without changing the producer.
- **Clients publish to Kafka directly:** rejected for the same reason as ADR 0001. Every client would depend on Kafka, and changing the broker would mean changing every client. Our API stays the only entry point.

## Consequences

**Positive**
- Bursts wait in Kafka instead of overloading the database. Consumers work at their own pace.
- Ingestion and processing scale separately (more API instances vs more consumers, up to the partition count).
- A database outage delays processing but doesn't lose events.
- Events can be replayed, and new consumers can be added later.

**Negative**
- More infrastructure to run: Kafka and Kafka UI.
- Machine clients don't get a final result in the response, only `202` with an `eventId`. Rejections are found in the DLT.
- Duplicates are expected and must be handled (the idempotency rules above).
- No transaction spans storage, Kafka and the database. If publishing fails after images are uploaded, the API returns an error, the client retries the ingest request with the same image keys, and orphaned staging images expire through the lifecycle rule.

**Follow-up**
- `GET /violations/ingest/{eventId}` so machine clients can check whether an event was processed or rejected.
- Add Kafka and Kafka UI to `docker-compose.yml` (KRaft mode, single broker for development).
- Dependency: `spring-kafka`.
