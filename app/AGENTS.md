# App module instructions

This module is the thin Android host.

- Own Android application startup, manifest, system bars and packaging only.
- Depend on `shared`; do not depend directly on features or core modules.
- Keep activities thin and launch the shared `RickAndMortyApp`.
- Do not read environment variables at runtime. Gradle resolves them at build time as documented in `docs/SECRETS.md`.
- Never expose a credential value in logs, tests, screenshots or documentation.

Validate app changes with:

```bash
./gradlew :app:lintDebug :app:assembleDebug
```
