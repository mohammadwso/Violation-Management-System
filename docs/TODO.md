# TODO

## In progress
- [ ] Violation processing

## Next

### Infrastructure
- [ ] ❓ Fix the AWS SDK dependency in `pom.xml`: `software.amazon.awssdk:bom` is in `groupId` (needs more explanation)
- [ ] Add Kafka (KRaft, single broker) and `kafbat/kafka-ui` to `docker-compose.yml`
- [ ] Add `spring-kafka` to `pom.xml`
- [ ] Add a ports table to the README (API 12000/12100, SQL 12001, MinIO 12003/12004, Kafka, Kafka UI)

### Storage ([ADR 0001](adr/0001-upload-violation-images-through-api.md))
- [ ] `storage.s3.*` properties per profile (local / docker) + a `@ConfigurationProperties` record
- [ ] S3 config: `S3Client` bean with endpoint override, path-style access, checksum `WHEN_REQUIRED`
- [ ] `ImageStorage` interface + `S3ImageStorage` (stream to `staging/`, promote to `violations/`, read, delete)
- [ ] Raise multipart limits (`max-file-size`, `max-request-size`) for 4K images
- [ ] `POST /violations/images`: check the real image type from its first bytes and the size, return `{ imageKey }`

### Database
- [ ] Liquibase: `violation_images` table (`violation_id`, `image_key` unique, `image_type`, `sha256`, `created_at`)
- [ ] Liquibase: drop `violation_image_key` from `violations`

### Creating violations ([ADR 0002](adr/0002-async-violation-ingestion-with-kafka.md), [ADR 0003](adr/0003-violation-service-design.md))
- [ ] `ViolationService`: shared private `create(...)` + `createFromInspector` / `createFromCctv` / `createFromVim`
- [ ] Idempotency: insert, catch `DataIntegrityViolationException` on `image_key`, return or skip the existing violation
- [ ] Check that image keys belong to the same source and are still in `staging/`
- [ ] Inspector endpoint `POST /violations` → 201
- [ ] Ingest endpoint `POST /violations/ingest` → publish to `violation.detected` (key = `sourceId`) → 202
- [ ] Kafka listener + `DefaultErrorHandler` with retries + dead-letter topic (`violation.detected.DLT`)

### Reading
- [ ] `GET /violations/{id}/images/{imageId}`: stream the image through the API

### Cross-cutting
- [ ] General API response wrapper for all endpoints (success + error in one shape)
- [ ] Logging AOP: aspect that logs method calls (args, result/exception, duration) for controllers/services

### Cleanup
- [ ] Move secrets (DB password, JWT secret, MinIO keys) out of committed properties into `.env` or environment variables
- [ ] README: users table, running commands

## Ideas / later
- [ ] `GET /violations/ingest/{eventId}`: let machine clients check processing status
- [ ] ⏳ Move `login` from `UserService` into an `AuthService`

## Done
- [x] ADRs 0001–0003 accepted (image upload, Kafka ingestion, violation service design)
- [x] MinIO in docker-compose (`pgsty/minio`): one `violations` bucket with `staging/` and `violations/` prefixes, plus a 1-day cleanup rule on `staging/`
- [x] Violation detail tables, remove remarks
- [x] Tables setup
- [x] Exception handling
- [x] Seed main roles, add user endpoint
- [x] Fix `@PreAuthorize`
