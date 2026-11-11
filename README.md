# Violation-Management-System

sysadmin

username: sysadmin

password: sysadmin

# Architecture

The project uses a classic **layered architecture**: `controller` → `service` → `repo`. Hexagonal architecture was considered and rejected as too much structure for this project. Details are in [ADR 0003](docs/adr/0003-violation-service-design.md).

**Creating violations:** one `ViolationService` with **one public method per source** (inspector, CCTV, VIM). Each method handles its source's own details, then calls **one shared private method** with the common steps: validate, promote images, save, and handle idempotency. Shared rules are written once, so sources can't drift apart.

- **Controllers** handle HTTP, and the **Kafka listener** handles messages. Both only call the service.
- **Services** hold the business rules and use JPA repositories directly.
- **`ImageStorage`** is an interface (`S3ImageStorage` today), so changing the storage service changes only one class ([ADR 0001](docs/adr/0001-upload-violation-images-through-api.md)).

# Design Decisions

Architecture decisions are recorded as ADRs in [docs/adr](docs/adr).

| ADR | Status | Decision |
|---|---|---|
| [0001](docs/adr/0001-upload-violation-images-through-api.md) | Accepted | Violation images are uploaded through our API (upload first, then create the violation with the keys) |
| [0002](docs/adr/0002-async-violation-ingestion-with-kafka.md) | Accepted | Two ways to create violations: synchronous for inspectors, asynchronous through Kafka for CCTV/VIM |
| [0003](docs/adr/0003-violation-service-design.md) | Accepted | Layered architecture, one `ViolationService` with a method per source |

# Todo

#### update readme

- users table for easy access
- running system commands
- table for ports of each svc 

