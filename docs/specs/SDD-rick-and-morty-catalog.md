# SDD — Rick and Morty catalog

Software Design Document. This is the source of truth for product behavior and architectural acceptance criteria.

| Field | Value |
|---|---|
| Status | Implemented |
| Version | 1.4 |
| Platforms | Android API 30–36; iOS/iPadOS 15+ |
| Backend | Rick and Morty REST API |
| Owner | Repository maintainers |

## Goal

Provide a backend-driven Compose catalog for every public Rick and Morty resource: characters, locations and episodes. The project must remain small enough to learn from while preserving boundaries that scale to a larger codebase.

## Out of scope

- Authentication or production credential storage.
- Offline persistence, search and filters.
- Cross-feature business rules.
- Separate native implementations of the product UI.
- Treating values embedded in an APK or iOS app as secret.

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
- `FR-017` — Android and iOS render the same Compose UI, navigation, presentation state, domain rules, repositories, network contract and resources from shared Kotlin source.
- `FR-018` — Each platform host contains only lifecycle/bootstrap code and the platform HTTP engine; no product behavior is duplicated.

## Design

```text
Android Activity / iOS SwiftUI host
  → shared Compose root
  → Compose event
  → Feature ViewModel
  → Domain use case
  → Domain repository interface
  → Data repository implementation
  → Ktor API / DTO
  → explicit mapper
  → CatalogResult<Page<DomainModel>, CatalogError>
  → immutable UiState
```

The dependency direction is inward. `core:domain` defines repository contracts; `core:data` implements them. Features know domain contracts and models, not Ktor or repository implementations. `shared` composes the features and data wiring. Android `app` and SwiftUI `iosApp` only host the shared root. All product UI and resources live in `commonMain`; only the Ktor OkHttp/Darwin engines and platform entry points use platform source sets.

## Configuration and security

Resolution order is environment variable, Gradle property, ignored `secrets.properties`, then an empty credential. The public base URL additionally has a safe default. Gradle generates a private shared Kotlin configuration object consumed by both clients. GitHub repository variables hold non-sensitive configuration; GitHub Secrets hold API keys/tokens. See `docs/SECRETS.md`.

This prevents accidental source-control exposure only. Long-lived private credentials must remain on a trusted backend.

## Acceptance criteria

- `AC-001` — A user can switch among all three resources and each destination renders backend content.
- `AC-002` — Page 1 produces exactly one of Loading → Content, Loading → Empty or Loading → Error.
- `AC-003` — Retry after an initial error requests page 1 again.
- `AC-004` — Loading page N appends its items once, updates totals and never requests beyond the last page.
- `AC-005` — A page-N failure leaves previous items visible and exposes a retry path.
- `AC-006` — Empty credential values produce no credential headers; configured values produce the four documented headers.
- `AC-007` — No credential value is printed by Gradle tasks, CI or Ktor logging.
- `AC-008` — Android compiles with compile/target SDK 36, min SDK 30 and Java 17; iOS compiles and links for arm64 devices and Apple Silicon simulators with deployment target 15.
- `AC-009` — Tapping a character shows its portrait, status, species, gender, origin, last known location, episode count and identifier; back restores the catalog and bottom navigation.
- `AC-010` — Connectivity, HTTP, serialization and unexpected failures map to typed domain errors; coroutine cancellation is never converted into a failure value.
- `AC-011` — Bottom-navigation selection follows the active route and restores destination state after switching tabs or returning from detail.
- `AC-012` — TalkBack can identify each catalog item as one coherent unit and receives loading/error state announcements.
- `AC-013` — The quality gate runs Android and iOS unit tests, Android lint, Detekt, Kotlin formatting checks, coverage verification, Android debug assembly, shared iOS framework linking and an unsigned iOS simulator build.
- `AC-014` — Character, location and episode lists have no manual load-more action; reaching the list threshold triggers one idempotent next-page request.
- `AC-015` — Location detail shows name, type, dimension, resident count and identifier; episode detail shows name, code, air date, character count and identifier.
- `AC-016` — Each of the 126 backend locations resolves to its own themed artwork, and the locations header marker floats vertically without moving the surrounding content.
- `AC-017` — Pressing the already-selected bottom-navigation destination scrolls its catalog smoothly back to the first item.
- `AC-018` — Character detail maps Alive, Dead and Unknown to distinct animated biometric treatments while keeping the portrait readable and the layout stationary.
- `AC-019` — All 126 location detail backgrounds cover the viewport without empty bands; dedicated portrait artwork is selected ahead of its landscape catalog source.
- `AC-020` — Location detail centers its screen label inside a large framed header and places the location name immediately below it without overlap.
- `AC-021` — Android and iOS consume the same `RickAndMortyApp`, feature screens, navigation graph, ViewModels, domain/data implementation and Compose resources from `commonMain`.
- `AC-022` — Android and iOS hosts contain no product screens, repository wiring or duplicated navigation; each delegates directly to the shared entry point.

## Non-functional requirements

- UI follows unidirectional data flow and lifecycle-aware state collection.
- Domain/model remain platform-free and unit-testable on Android host and Kotlin/Native.
- Dependency versions are centralized and stable.
- CI runs Android unit tests, lint, static analysis, formatting, scoped coverage and debug assembly on Linux, plus iOS tests/framework/host compilation on macOS, with read-only repository permissions.
- Static analysis, formatting and coverage thresholds are executable locally and in CI.
- Kover measures an explicit application-logic scope: domain, data mappers/repositories/credential headers, feature UI-state models and ViewModels. Generated code, resources, themes and Compose rendering are outside that metric and use separate checks.

## Definition of done

1. Changed behavior has updated `FR-*`/`AC-*` entries.
2. The TDD maps affected acceptance criteria to automated or explicit manual evidence.
3. No forbidden module dependency or credential exposure is introduced.
4. `./gradlew testAndroidHostTest lintDebug detekt ktlintCheck :koverVerify :app:assembleDebug --warning-mode all` passes.
5. `./gradlew iosSimulatorArm64Test :shared:linkDebugFrameworkIosSimulatorArm64 :shared:linkDebugFrameworkIosArm64 --warning-mode all` passes on macOS.
6. The unsigned `iosApp` arm64 simulator build passes with `xcodebuild`.
