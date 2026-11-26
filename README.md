# Violation-Management-System

sysadmin

username: sysadmin

password: sysadmin

# Architecture

- Layered: `controller` → `service` → `repo`. Hexagonal architecture was considered and rejected as too much for this project.
- One `ViolationService` holds the violation business rules, and it uses JPA repositories directly.
- Storage sits behind an interface: `StorageService`, implemented by `S3StorageService`. Changing the storage provider changes only that class.

# Images and storage

- **Flow: upload first, then create.**
  - `POST /violations/images` uploads the image to `staging/` and returns `{ imageKey }`.
  - `POST /violations` checks that the key exists, then copies the image to `violations/` (promote).
- **Preferred, not enforced (learning project):** only our app talks to storage. Clients never get storage credentials or URLs, so the provider (MinIO → S3, etc.) can change without touching clients. The bucket is private (`mc anonymous set none`).
- **One bucket, two prefixes:** `violations` bucket with `staging/` and `violations/`. Keys look like `staging/<source>/<uuid>`.
- **Automatic cleanup:** a MinIO lifecycle rule deletes `staging/` objects after 1 day (`init-minio` in `docker-compose.yml`). Unused uploads need no cleanup code.
- **Upload limit:** `spring.servlet.multipart.max-file-size=10MB`.
- **Rejected options:**
  - One multipart request with data + images: too large, a failure re-uploads everything.
  - Create, then attach images: violations can exist without evidence.
  - Presigned URLs: deferred, same client flow, can switch later.

# Creating violations

- Only the **synchronous inspector path** exists: `POST /violations` creates the violation right away.

# Todo

#### update readme

- users table for easy access
- running system commands
- table for ports of each svc 

