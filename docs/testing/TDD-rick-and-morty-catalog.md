# TDD — Rick and Morty catalog

Test Design Document for [SDD version 1.3](../specs/SDD-rick-and-morty-catalog.md). Development follows the test-first loop: reproduce or specify a failing behavior, implement the smallest change, then refactor while the suite remains green.

## Test strategy

1. Pure unit tests protect domain delegation and state transitions quickly.
2. Data tests protect request configuration, mapping and repository failure behavior.
3. Compose tests cover semantics/navigation that lower layers cannot prove.
4. A small emulator smoke test validates the real backend and platform integration.

No test may assert, snapshot or print a real credential value.

## Acceptance traceability

| Criterion | Evidence | Module | Current status |
|---|---|---|---|
| `AC-001` | Navigate all destinations; verify resource content | `app` + features | Feature states automated; API 30 real-backend smoke passed |
| `AC-002` | Loading resolves to content, empty or typed error | `feature:*` | Automated for all three ViewModels |
| `AC-003` | Failure followed by `retry()` requests page 1 again | `feature:*` | Automated for all three ViewModels |
| `AC-004` | Append next page and reject duplicate/end-of-list loads | `feature:*` | Automated for all three ViewModels |
| `AC-005` | Load-more failure preserves content and marks retry state | `feature:*` | Automated for all three ViewModels |
| `AC-006` | Present and blank credential header cases | `core:data` | Automated |
| `AC-007` | Header redaction review; Gradle tasks report names/sources only | `core:data` + build | Automated plus review |
| `AC-008` | Unit tests, lint and debug assembly | whole project | CI gate |
| `AC-009` | Tap a character, verify its fields and back navigation | `feature:characters` + `app` | Card action automated; API 30 detail/back smoke passed |
| `AC-010` | Data-boundary error mapping and cancellation propagation tests | `core:data` + `core:domain` | Automated |
| `AC-011` | Switch destinations and verify selected route/state restoration | `app` | Selection and model saver round trips automated; API 30 route restoration smoke passed |
| `AC-012` | Assert grouped item semantics and announced state descriptions | `feature:*` | Grouping/loading automated; API 30 error/retry smoke passed |
| `AC-013` | Detekt, formatting, coverage verification and assembly | whole project | CI gate |
| `AC-014` | Scroll near the end and verify automatic, idempotent pagination | `feature:*` | ViewModel automation; API 30 pagination smoke passed |
| `AC-015` | Open location and episode details, verify fields and back restoration | `feature:*` + `app` | Card actions automated; API 30 detail/back smoke passed |
| `AC-016` | Verify artwork IDs 1–126 are unique resources and observe the header marker at two animation frames | `feature:locations` | Resolver automation plus API 30 visual smoke |
| `AC-017` | Scroll each catalog, reselect its active bottom-navigation item and verify the first item is restored | `app` + `feature:*` | API 30 interaction smoke |
| `AC-018` | Open Alive, Dead and Unknown character details; capture two frames and verify distinct status effects without portrait occlusion or layout movement | `feature:characters` | Status mapping automated plus API 30 visual smoke |
| `AC-019` | Verify all detail backgrounds cover the viewport and portrait overrides resolve ahead of list artwork | `feature:locations` | Resolver automation plus API 30 visual smoke |
| `AC-020` | Open a location detail and verify the centered framed header, title placement and lack of overlap | `feature:locations` | API 30 visual smoke |

## Required test cases for new resource behavior

- Use case delegates the requested page and returns the repository `CatalogResult` unchanged.
- Mapper converts every backend field used by the domain model and pagination metadata.
- Repository classifies connectivity, HTTP, serialization and unexpected failures without swallowing cancellation.
- ViewModel covers initial content, empty, error, retry, next page, duplicate next-page request, last page and load-more failure.
- Screen semantics expose retry/action labels and stable list identity.
- Root navigation derives selection from the active route and restores destination state.
- Large-font and dark-theme previews cover every catalog and detail screen.

## CI gate

```bash
./gradlew test lintDebug detekt ktlintCheck :koverVerify assembleDebug --warning-mode all
```

CI must also run `secretsStatus` to prove configuration wiring without requiring credentials on pull requests from forks.

Kover verifies at least 60% line coverage over the configured application-logic scope: domain, data mappers/repositories, the credentials interceptor, feature UI-state models and ViewModels. Generated code, resources, themes and Compose screen rendering are excluded deliberately; instrumented UI tests provide separate evidence.

## Instrumented Compose gate

With a connected device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest \
  :feature:characters:connectedDebugAndroidTest \
  :feature:locations:connectedDebugAndroidTest \
  :feature:episodes:connectedDebugAndroidTest
```

These tests verify bottom-navigation selection and its large-text/compact-width adaptations, actionable grouped catalog semantics and polite loading announcements. They do not replace the real-backend and interruption scenarios below.

## Emulator smoke checklist

- API 30: launch, navigate to all tabs, load images and request a second page.
- API 36: repeat navigation and pagination; verify edge-to-edge layout.
- Disable network: verify initial error and retry.
- Interrupt page 2: verify page 1 remains visible and retry succeeds.
- Open a character, verify every displayed field, press back and verify the list and bottom navigation are restored.
- Scroll each catalog to its end and verify the next page loads without a manual button.
- Open a location and an episode, verify their detail fields, then press back and verify list restoration.
- Scroll through locations, verify that all 126 identifiers resolve to distinct artwork, and capture the header marker at two animation frames without layout movement.
- Scroll each catalog away from the header, press its selected bottom-navigation item again and verify a smooth return to the first item.
- Open Alive, Dead and Unknown character details and verify their live, stopped and quantum biometric animations at two frames without portrait occlusion.
- Open representative portrait and landscape-backed location details; verify edge-to-edge coverage, sharp filtering and readable overlays.
- Open a location detail and verify the centered framed header remains separate from the back action, with the location name directly below and no overlap.
- Enable a large system font and dark theme: verify list/detail readability, touch targets and that no content is clipped.

## Evidence record

For each release or architectural change record the command/device, result, failing test if any and the affected `AC-*` identifiers. Never replace missing evidence with an assumption.

### 2026-07-20 — `emulator-5556`, Android 11 (API 30)

- PASS: debug installation, all three destinations and real-backend content (`AC-001`).
- PASS: character, location and episode list → detail → back, preserving the selected destination (`AC-009`, `AC-011`, `AC-015`).
- PASS: automatic pagination from 20 to 60 characters and from 20 to 51 episodes; transient append failure recovered through retry (`AC-004`, `AC-005`, `AC-014`).
- PASS: offline initial state exposed a specific error and retry restored the first 20 characters (`AC-003`, `AC-012`).
- PASS: Alive, Dead and Unknown character details rendered distinct animated biometric states without covering the portrait or moving the layout (`AC-018`).
- PASS: all 126 location backgrounds resolved with high-quality full-viewport rendering; Hideout Planet used its dedicated portrait asset on API 30 (`AC-019`).
- PASS: location detail rendered its centered framed header and location name directly below without overlap (`AC-020`).
- PASS: zero package crashes, FATAL entries or ANRs in Logcat.
- Pending: repeat the platform smoke test on API 36, including edge-to-edge, large-font and dark-theme checks.

### 2026-07-21 — `emulator-5556`, Android 11 (API 30)

- PASS: all 52 JVM tests and 11 instrumented Compose tests completed successfully (`AC-013`).
- PASS: all 126 location identifiers resolve to distinct themed artwork resources (`AC-016`).
- PASS: the header marker moves vertically between captured frames while the title, count and list remain fixed (`AC-016`).
- PASS: reselecting Characters, Locations or Episodes after scrolling restores the corresponding header (`AC-017`).
