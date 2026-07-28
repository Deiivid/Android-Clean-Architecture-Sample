# TDD — Rick and Morty catalog

Test Design Document for [SDD version 1.4](../specs/SDD-rick-and-morty-catalog.md). Development follows the test-first loop: reproduce or specify a failing behavior, implement the smallest change, then refactor while the suite remains green.

## Test strategy

1. `commonTest` protects domain delegation, mapping, repositories and presentation state on Android host and iOS simulator.
2. Ktor `MockEngine` tests protect request-independent failure mapping and malformed/truncated payload handling.
3. Android Compose instrumentation covers semantics/navigation that lower layers cannot prove.
4. Android and iOS host builds validate platform integration; focused device/simulator smoke tests validate the real backend.

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
| `AC-008` | Android assembly plus iOS framework and host build | whole project | Automated local/CI gates |
| `AC-009` | Tap a character, verify its fields and back navigation | `feature:characters` + `app` | Card action automated; API 30 detail/back smoke passed |
| `AC-010` | Data-boundary error mapping and cancellation propagation tests | `core:data` + `core:domain` | Automated |
| `AC-011` | Switch destinations and verify selected route/state restoration | `app` | Selection and model saver round trips automated; API 30 route restoration smoke passed |
| `AC-012` | Assert grouped item semantics and announced state descriptions | `feature:*` | Grouping/loading automated; API 30 error/retry smoke passed |
| `AC-013` | Android/iOS tests, Detekt, formatting, coverage and both host builds | whole project | CI gate |
| `AC-014` | Scroll near the end and verify automatic, idempotent pagination | `feature:*` | ViewModel automation; API 30 pagination smoke passed |
| `AC-015` | Open location and episode details, verify fields and back restoration | `feature:*` + `app` | Card actions automated; API 30 detail/back smoke passed |
| `AC-016` | Verify artwork IDs 1–126 are unique resources and observe the header marker at two animation frames | `feature:locations` | Resolver automation plus API 30 visual smoke |
| `AC-017` | Scroll each catalog, reselect its active bottom-navigation item and verify the first item is restored | `app` + `feature:*` | API 30 interaction smoke |
| `AC-018` | Open Alive, Dead and Unknown character details; capture two frames and verify distinct status effects without portrait occlusion or layout movement | `feature:characters` | Status mapping automated plus API 30 visual smoke |
| `AC-019` | Verify all detail backgrounds cover the viewport and portrait overrides resolve ahead of list artwork | `feature:locations` | Resolver automation plus API 30 visual smoke |
| `AC-020` | Open a location detail and verify the centered framed header, title placement and lack of overlap | `feature:locations` | API 30 visual smoke |
| `AC-021` | Compile shared UI and tests for Android host and iOS simulator; link the iOS framework | `shared` + all KMP modules | Automated |
| `AC-022` | Inspect both hosts and build Android APK/iOS app without duplicated product behavior | `app` + `iosApp` | Automated build plus architecture review |

## Required test cases for new resource behavior

- Use case delegates the requested page and returns the repository `CatalogResult` unchanged.
- Mapper converts every backend field used by the domain model and pagination metadata.
- Repository classifies connectivity, HTTP, Ktor serialization and unexpected failures without swallowing cancellation.
- ViewModel covers initial content, empty, error, retry, next page, duplicate next-page request, last page and load-more failure.
- Screen semantics expose retry/action labels and stable list identity.
- Root navigation derives selection from the active route and restores destination state.
- Large-font and dark-theme previews cover every catalog and detail screen.

## CI gate

```bash
./gradlew testAndroidHostTest lintDebug detekt ktlintCheck :koverVerify \
  :app:assembleDebug --warning-mode all
./gradlew iosSimulatorArm64Test \
  :shared:linkDebugFrameworkIosSimulatorArm64 \
  :shared:linkDebugFrameworkIosArm64 --warning-mode all
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

CI must also run `secretsStatus` to prove configuration wiring without requiring credentials on pull requests from forks. The Android gate runs on Linux and the iOS gate runs on macOS.

Kover verifies at least 60% line coverage over the configured application-logic scope: domain, data mappers/repositories/credential headers, feature UI-state models and ViewModels. Generated code, resources, themes and Compose screen rendering are excluded deliberately; instrumented UI tests provide separate evidence.

## Instrumented Compose gate

With a connected device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest \
  :shared:connectedAndroidDeviceTest \
  :feature:characters:connectedAndroidDeviceTest \
  :feature:locations:connectedAndroidDeviceTest \
  :feature:episodes:connectedAndroidDeviceTest
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
- iOS simulator: repeat destination, detail/back, retry and pagination flows and verify Compose resources and remote character images render.

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

### 2026-07-28 — Android 16 device and iOS 26 arm64 simulator

- PASS: all shared unit tests completed on Android host and iOS simulator (`AC-002`–`AC-010`, `AC-021`).
- PASS: Android debug APK assembled from the thin `app` host and shared application (`AC-008`, `AC-022`).
- PASS: the `Shared.framework` linked for `iosArm64` and `iosSimulatorArm64`, and the SwiftUI `iosApp` built unsigned with Xcode 26.0.1 (`AC-008`, `AC-021`, `AC-022`).
- PASS: all shared and feature connected-device suites completed on a physical SM-A536B running Android 16/API 36, including navigation, large-text and grouped-semantics checks (`AC-011`–`AC-013`, `AC-021`).
- PASS: the Android host launched against the public backend, rendered 20 of 826 characters with remote portraits and remained free of fatal process errors (`AC-001`, `AC-008`, `AC-022`).
- PASS: the unsigned host launched on an iPhone 17 Pro/iOS 26 simulator, rendered the same shared catalog, localized resources and remote portraits, and remained alive after loading (`AC-001`, `AC-008`, `AC-021`, `AC-022`).
