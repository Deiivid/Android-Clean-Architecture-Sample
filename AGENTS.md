# Repository instructions

## Mission

Maintain this repository as a small, production-grade Kotlin Multiplatform reference for Clean Architecture and Compose Multiplatform on Android and iOS. Prefer explicit boundaries, observable behavior and tests over decorative abstractions.

## Load only the context needed

1. Read this file, then the nearest nested `AGENTS.md` for files being changed.
2. Read [the SDD](docs/specs/SDD-rick-and-morty-catalog.md) when behavior, scope or acceptance criteria change.
3. Read [the TDD](docs/testing/TDD-rick-and-morty-catalog.md) when tests or acceptance coverage change.
4. Read [the secrets guide](docs/SECRETS.md) only for build configuration, CI or credentials.

Do not scan generated `build/` folders or load every document by default.

## Dependency rule

```text
app ──> shared <── iosApp
          ├──> feature:* ──> core:domain ──> core:model
          └──> core:data ──> core:domain ──> core:model
```

- `app` and `iosApp` are thin platform hosts; `shared` is the composition root.
- `core:model` and `core:domain` are framework-free Kotlin.
- `core:data` implements domain contracts; domain never imports data.
- A feature depends on domain/model, never on data or another feature.

## Global rules

- Keep user-facing text in resources and UI state immutable.
- Keep business logic outside activities and composables.
- Never commit or log credential values. Values compiled into an APK or iOS app are extractable.
- Add dependencies through `gradle/libs.versions.toml`; use stable, mutually compatible versions.
- Update SDD acceptance criteria first for intentional behavior changes, then map them to tests in the TDD.
- Preserve unrelated local changes and do not commit, push or alter Git history unless requested.

## Validation

Use the narrowest useful task while iterating. Before handoff of cross-module changes run:

```bash
./gradlew testAndroidHostTest lintDebug detekt ktlintCheck :koverVerify :app:assembleDebug --warning-mode all
./gradlew iosSimulatorArm64Test :shared:linkDebugFrameworkIosSimulatorArm64 \
  :shared:linkDebugFrameworkIosArm64 --warning-mode all
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build
```

Do not report a command as passing unless it actually ran.
