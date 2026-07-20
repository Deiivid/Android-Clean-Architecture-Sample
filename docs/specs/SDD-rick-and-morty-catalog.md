# SDD — Rick and Morty catalog

Software Design Document. This is the source of truth for product behavior and architectural acceptance criteria.

| Field | Value |
|---|---|
| Status | Implemented |
| Version | 1.0 |
| Platforms | Android API 30–36 |
| Backend | Rick and Morty REST API |
| Owner | Repository maintainers |

## Goal

Provide a backend-driven Compose catalog for every public Rick and Morty resource: characters, locations and episodes. The project must remain small enough to learn from while preserving boundaries that scale to a larger codebase.

## Out of scope

- Authentication or production credential storage.
- Offline persistence, search, filters and detail screens.
- Cross-feature business rules.
- Treating values embedded in an APK as secret.

## Functional requirements

- `FR-001` — The root UI exposes Characters, Locations and Episodes as independent destinations.
- `FR-002` — Entering a destination requests page 1 of its matching backend resource.
- `FR-003` — Every destination represents loading, content, empty and initial-load error states and offers retry after error.
- `FR-004` — Content exposes server totals and can append subsequent pages until the server-reported last page.
- `FR-005` — A load-more request cannot run twice concurrently. A load-more failure keeps current content and allows retry.
- `FR-006` — Switching destinations preserves the saveable UI state of each destination.
- `FR-007` — Blank credentials are omitted. Configured credentials become request headers and those headers are redacted from HTTP logs.
- `FR-008` — The base URL and demo credentials can be supplied by environment variables without storing their values in Git.

## Design

```text
Compose event
  → Feature ViewModel
  → Domain use case
  → Domain repository interface
  → Data repository implementation
  → Retrofit API / DTO
  → explicit mapper
  → Result<Page<DomainModel>>
  → immutable UiState
```

The dependency direction is inward. `core:domain` defines repository contracts; `core:data` implements them. Features know domain contracts and models, not Retrofit or repository implementations. `app` is the only module that composes all features and data wiring.

## Configuration and security

Resolution order is environment variable, Gradle property, ignored `secrets.properties`, then an empty credential. The public base URL additionally has a safe default. GitHub repository variables hold non-sensitive configuration; GitHub Secrets hold API keys/tokens. See `docs/SECRETS.md`.

This prevents accidental source-control exposure only. Long-lived private credentials must remain on a trusted backend.

## Acceptance criteria

- `AC-001` — A user can switch among all three resources and each destination renders backend content.
- `AC-002` — Page 1 produces exactly one of Loading → Content, Loading → Empty or Loading → Error.
- `AC-003` — Retry after an initial error requests page 1 again.
- `AC-004` — Loading page N appends its items once, updates totals and never requests beyond the last page.
- `AC-005` — A page-N failure leaves previous items visible and exposes a retry path.
- `AC-006` — Empty credential values produce no credential headers; configured values produce the four documented headers.
- `AC-007` — No credential value is printed by Gradle tasks, CI or OkHttp logging.
- `AC-008` — The project compiles with compile/target SDK 36, min SDK 30 and Java 17.

## Non-functional requirements

- UI follows unidirectional data flow and lifecycle-aware state collection.
- Domain/model remain Android-free and unit-testable on the JVM.
- Dependency versions are centralized and stable.
- CI runs unit tests, lint and debug assembly with read-only repository permissions.

## Definition of done

1. Changed behavior has updated `FR-*`/`AC-*` entries.
2. The TDD maps affected acceptance criteria to automated or explicit manual evidence.
3. No forbidden module dependency or credential exposure is introduced.
4. `./gradlew test lintDebug assembleDebug --warning-mode all` passes.
