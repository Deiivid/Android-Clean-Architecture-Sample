# SDD — Rick and Morty catalog

Software Design Document. This is the source of truth for product behavior and architectural acceptance criteria.

| Field | Value |
|---|---|
| Status | Implemented |
| Version | 1.3 |
| Platforms | Android API 30–36 |
| Backend | Rick and Morty REST API |
| Owner | Repository maintainers |

## Goal

Provide a backend-driven Compose catalog for every public Rick and Morty resource: characters, locations and episodes. The project must remain small enough to learn from while preserving boundaries that scale to a larger codebase.

## Out of scope

- Authentication or production credential storage.
- Offline persistence, search and filters.
- Cross-feature business rules.
- Treating values embedded in an APK as secret.

## Functional requirements

- `FR-001` — The root UI exposes Characters, Locations and Episodes as independent destinations.
- `FR-002` — Entering a destination requests page 1 of its matching backend resource.
- `FR-003` — Every destination represents loading, content, empty and initial-load error states and offers retry after error.
- `FR-004` — Content exposes server totals and automatically appends subsequent pages when the user approaches the end of the list, until the server-reported last page.
- `FR-005` — A load-more request cannot run twice concurrently. A load-more failure keeps current content and allows retry.
- `FR-006` — Switching destinations preserves the saveable UI state of each destination.
- `FR-007` — Blank credentials are omitted. Configured credentials become request headers and those headers are redacted from HTTP logs.
- `FR-008` — The base URL and demo credentials can be supplied by environment variables without storing their values in Git.
- `FR-009` — Selecting a character opens a detail destination backed by the already loaded domain model; back returns to the preserved character list.
- `FR-010` — Data failures are translated into a finite domain error model. Cancellation is propagated and presentation never receives a raw exception.
- `FR-011` — Root destinations use route-based navigation with a single source of truth for selection and state restoration.
- `FR-012` — Loading, error and incremental-loading changes are announced to accessibility services; repeated cards expose concise grouped semantics.
- `FR-013` — User-facing labels are localized and layouts remain usable with large text and dark theme.
- `FR-014` — Selecting a location or episode opens its detail destination from the loaded domain model; back restores the corresponding list.
- `FR-015` — Character detail visualizes status without covering the portrait: a live heartbeat for Alive, a stopped heartbeat for Dead and an unstable quantum orbit for Unknown.
- `FR-016` — Location detail renders every artwork edge to edge with high-quality filtering and prefers a dedicated portrait asset when one is available.

## Design

```text
Compose event
  → Feature ViewModel
  → Domain use case
  → Domain repository interface
  → Data repository implementation
  → Retrofit API / DTO
  → explicit mapper
  → CatalogResult<Page<DomainModel>, CatalogError>
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
- `AC-009` — Tapping a character shows its portrait, status, species, gender, origin, last known location, episode count and identifier; back restores the catalog and bottom navigation.
- `AC-010` — Connectivity, HTTP, serialization and unexpected failures map to typed domain errors; coroutine cancellation is never converted into a failure value.
- `AC-011` — Bottom-navigation selection follows the active route and restores destination state after switching tabs or returning from detail.
- `AC-012` — TalkBack can identify each catalog item as one coherent unit and receives loading/error state announcements.
- `AC-013` — The quality gate runs unit tests, Android lint, Detekt, Kotlin formatting checks, coverage verification and debug assembly.
- `AC-014` — Character, location and episode lists have no manual load-more action; reaching the list threshold triggers one idempotent next-page request.
- `AC-015` — Location detail shows name, type, dimension, resident count and identifier; episode detail shows name, code, air date, character count and identifier.
- `AC-016` — Each of the 126 backend locations resolves to its own themed artwork, and the locations header marker floats vertically without moving the surrounding content.
- `AC-017` — Pressing the already-selected bottom-navigation destination scrolls its catalog smoothly back to the first item.
- `AC-018` — Character detail maps Alive, Dead and Unknown to distinct animated biometric treatments while keeping the portrait readable and the layout stationary.
- `AC-019` — All 126 location detail backgrounds cover the viewport without empty bands; dedicated portrait artwork is selected ahead of its landscape catalog source.
- `AC-020` — Location detail centers its screen label inside a large framed header and places the location name immediately below it without overlap.

## Non-functional requirements

- UI follows unidirectional data flow and lifecycle-aware state collection.
- Domain/model remain Android-free and unit-testable on the JVM.
- Dependency versions are centralized and stable.
- CI runs unit tests, lint, static analysis, formatting, scoped coverage verification and debug assembly with read-only repository permissions.
- Static analysis, formatting and coverage thresholds are executable locally and in CI.
- Kover measures an explicit application-logic scope: domain, data mappers/repositories, the credentials interceptor, feature UI-state models and ViewModels. Generated code, resources, themes and Compose rendering are outside that metric and use separate checks.

## Definition of done

1. Changed behavior has updated `FR-*`/`AC-*` entries.
2. The TDD maps affected acceptance criteria to automated or explicit manual evidence.
3. No forbidden module dependency or credential exposure is introduced.
4. `./gradlew test lintDebug detekt ktlintCheck :koverVerify assembleDebug --warning-mode all` passes.
