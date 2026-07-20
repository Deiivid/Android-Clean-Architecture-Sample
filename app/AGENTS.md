# App module instructions

This module is the Android composition root.

- Own application startup, root navigation, theme, manifest and Hilt wiring across modules.
- Convert build-time configuration into typed objects such as `NetworkConfiguration` and `ApiCredentials`.
- Depend on feature entry points and `core:data`; do not implement repositories, DTOs or business rules here.
- Keep activities thin. Compose content should delegate feature behavior to feature routes/ViewModels.
- Do not read environment variables at runtime. Gradle resolves them at build time as documented in `docs/SECRETS.md`.
- Never expose a credential value in logs, tests, screenshots or documentation.

Validate app changes with:

```bash
./gradlew :app:lintDebug :app:assembleDebug
```
