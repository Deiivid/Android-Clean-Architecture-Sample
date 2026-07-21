# Repository instructions

## Mission

Maintain this repository as a small, production-grade Android reference for Clean Architecture, Kotlin and Jetpack Compose. Prefer explicit boundaries, observable behavior and tests over decorative abstractions.

## Load only the context needed

1. Read this file, then the nearest nested `AGENTS.md` for files being changed.
2. Read [the SDD](docs/specs/SDD-rick-and-morty-catalog.md) when behavior, scope or acceptance criteria change.
3. Read [the TDD](docs/testing/TDD-rick-and-morty-catalog.md) when tests or acceptance coverage change.
4. Read [the secrets guide](docs/SECRETS.md) only for build configuration, CI or credentials.

Do not scan generated `build/` folders or load every document by default.

## Dependency rule

```text
app ──> feature:* ──> core:domain ──> core:model
  └────> core:data ──> core:domain ──> core:model
```

- `app` is the composition root.
- `core:model` and `core:domain` are framework-free Kotlin.
- `core:data` implements domain contracts; domain never imports data.
- A feature depends on domain/model, never on data or another feature.

## Global rules

- Keep user-facing text in resources and UI state immutable.
- Keep business logic outside activities and composables.
- Never commit or log credential values. Client-side `BuildConfig` values are extractable from an APK.
- Add dependencies through `gradle/libs.versions.toml`; use stable, mutually compatible versions.
- Update SDD acceptance criteria first for intentional behavior changes, then map them to tests in the TDD.
- Preserve unrelated local changes and do not commit, push or alter Git history unless requested.

## Validation

Use the narrowest useful task while iterating. Before handoff of cross-module changes run:

```bash
./gradlew test lintDebug detekt ktlintCheck :koverVerify assembleDebug --warning-mode all
```

Do not report a command as passing unless it actually ran.
