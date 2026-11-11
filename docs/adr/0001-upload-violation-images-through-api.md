# ADR 0001: Upload violation images through our API

- **Status:** Accepted
- **Date:** 2026-11-11
- **Related:** [ADR 0002](0002-async-violation-ingestion-with-kafka.md), [ADR 0003](0003-violation-service-design.md)

## Context

Every violation needs image evidence. A violation can have **several images** (e.g. scene, plate close-up), and CCTV cameras send 4K images (about 1.5–5 MB each) in large volumes. Images are stored in S3-compatible object storage (MinIO for local development). The database stores only object keys.

Several apps and pipelines that use our business logic create violations: CCTV, VIM and inspectors. We had to decide how their images reach storage.

## Options considered

1. **One multipart request.** The client sends the violation data and all images together to `POST /violations`.
2. **Create the violation, then attach images** in later requests.
3. **Upload images first, then create the violation with their keys.** Unused uploads expire after a retention period. There are two variants:
   - **3a. Presigned URL:** our app returns a signed URL, and the client uploads directly to storage.
   - **3b. Through our API:** the client uploads to our endpoint, and our app streams the image into storage.

## Decision

We use **option 3b**.

1. `POST /violations/images` → our app streams the image into `staging/` and returns `{ imageKey }`. One request per image.
2. The violation is created with `imageKeys: [...]`. The app checks that each key exists, is in `staging/`, and was uploaded by the same source, then moves it to `violations/`.
3. Images left in `staging/` are deleted by a storage lifecycle rule after 1 day.

Only our app talks to the storage service.

## Rationale

- **No external service is connected to the storage service.** Only our app knows which storage we use, where it is, and its credentials. Switching providers (MinIO → AWS S3, RustFS, etc.), moving storage, or rotating credentials changes only our app.
- **It handles multiple large images well.** Each image is uploaded and retried on its own. A failed violation request doesn't force the client to re-upload images.
- **The violation request stays small JSON**, so it's easy to validate (`@Valid`), test, and put on a message queue ([ADR 0002](0002-async-violation-ingestion-with-kafka.md)).
- **Cleanup is automatic.** Orphaned uploads expire through the lifecycle rule. No cleanup code is needed.
- **Option 1 was rejected:** with several 4K images, one request is 10–25 MB, and any failure means re-uploading everything.
- **Option 2 was rejected:** violations could exist without evidence if the later requests never arrive.
- **Option 3a is deferred, not rejected.** The client only receives an opaque URL and does a plain HTTP `PUT`. It has no storage SDK, no credentials and doesn't know the provider, so the decoupling mostly holds. Its costs: storage must be reachable from client networks, and images can only be checked after upload. Because the client flow has the same shape (get key → upload → send keys), we can switch to 3a later if upload traffic overloads our app. The client contract stays the same.

## Consequences

**Positive**
- Storage is an internal detail of our app.
- Our app checks every image (real type from its first bytes, and size) before it's stored.
- Storage credentials never leave our app.

**Negative**
- Every image passes through our app, which uses its bandwidth. Uploads must be **streamed** to storage, not loaded fully into memory.
- Upload limits must be set: `spring.servlet.multipart.max-file-size` / `max-request-size` (the defaults of 1 MB / 10 MB are too small for 4K images), plus any proxy limits.
- Clients make one request per image plus one for the violation.
- Our app must check that a client only uses keys it uploaded itself (staging keys are scoped by source: `staging/<sourceId>/<uuid>`).

**Follow-up**
- Schema: replace the single `violation_image_key` column with a `violation_images` table (`violation_id`, `image_key`, `image_type`, `sha256`, `created_at`). `image_key` is **unique**, because it's also the idempotency key ([ADR 0002](0002-async-violation-ingestion-with-kafka.md)). The SHA-256 is for evidence integrity and isn't unique.
- Reading images: serve them through our API (e.g. `GET /violations/{id}/images/{imageId}`) to stay consistent with this decision.
