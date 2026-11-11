# ADR 0003: Layered architecture with one violation service and per-source methods

- **Status:** Accepted
- **Date:** 2026-11-11
- **Related:** [ADR 0001](0001-upload-violation-images-through-api.md), [ADR 0002](0002-async-violation-ingestion-with-kafka.md)

## Context

Violations are created from several sources (ADR 0002):

- **Inspector**, synchronously through `POST /violations`
- **CCTV** and **VIM**, asynchronously through `POST /violations/ingest` → Kafka → consumer

Each source sends **different details** (an inspector is a logged-in user, a camera has a device ID, and so on). But they all share the same core steps: validate reference data (region, type), check and promote images, save the violation and its images, and handle idempotency (unique image keys).

This is a first backend project, and the goal is to learn Spring, JPA and Kafka well. Hexagonal architecture (ports, adapters, commands, separate domain models) was considered and **rejected as overkill** for this project.

## Decision

Keep the **existing layered structure** (`controller` → `service` → `repo`) and use **one `ViolationService`** with:

- **One public method per source.** Each takes that source's own DTO and handles its source-specific details:
  - `createFromInspector(InspectorViolationRequest request, User inspector)`
  - `createFromCctv(CctvViolationEvent event)`
  - `createFromVim(VimViolationEvent event)`
- **One private method with the shared steps** that every public method calls: validate reference data, promote images, save, and catch the unique-constraint violation for idempotency.

```
ViolationController ──▶ createFromInspector ──┐
                                              │
Kafka listener ──┬───▶ createFromCctv ────────┼──▶ private create(...) ──▶ repos, ImageStorage
                 └───▶ createFromVim ─────────┘
```

**When to split it:** if a source's specific logic grows large (many rules of its own), move that source's code into its own class, e.g. `CctvViolationService`. It calls a shared class (e.g. `ViolationCreator`) that holds the common steps. Shared code is reused through **composition** (calling a shared class), not by copying it.

**Infrastructure:** `ImageStorage` stays an interface (implemented by `S3ImageStorage`), so changing the storage service changes only one class (ADR 0001).

## Alternatives considered

- **An interface with one implementation per source** (`ViolationCreationService` with `InspectorViolationService`, `CctvViolationService`, `VimViolationService`). Rejected for now:
  - Each source has a **different input type**, so the interface can't have one common method signature without a shared input object. That shared object is the command we rejected.
  - Each caller already knows which source it handles: the inspector controller always needs the inspector logic. An interface helps when the caller **doesn't** know which implementation it gets (polymorphism), which isn't the case here.
  - The shared steps would still need a home (a base class or a helper), or they would be duplicated three times.
  - Worth revisiting if CCTV and VIM arrive on the same topic and the listener has to choose the logic by source type at runtime. That's the Strategy pattern, and there an interface pays off.
- **Hexagonal architecture** (full or pragmatic): rejected as too much structure for this project's size and learning goals.
- **Commands** (one neutral input object for all sources): rejected. Each source keeps its own DTO.

## Consequences

**Positive**
- A familiar, simple structure that matches the rest of the project and most Spring tutorials.
- Shared rules live in one private method, so sources can't drift apart.
- Source-specific details are clearly separated by method.
- Easy to split per source later without changing callers much.

**Negative**
- `ViolationService` can grow large as sources and rules grow. Watch its size and split it as described above when needed.
- The service depends on each source's DTO, so a DTO change touches the service.
- Services use JPA repositories directly, so persistence details can reach business logic. Accepted for simplicity.
