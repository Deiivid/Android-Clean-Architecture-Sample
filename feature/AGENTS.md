# Feature modules instructions

Each feature owns one vertical presentation slice.

- Depend only on `core:domain`, `core:model` and UI libraries.
- A route receives its ViewModel from `shared`, collects lifecycle-aware state and delegates rendering to a stateless screen.
- Expose immutable `StateFlow` and model loading, content, empty and error states explicitly.
- Keep pagination idempotent: prevent concurrent loads, append successful pages and preserve existing content on load-more failure.
- Keep composables small, resource-backed, previewable and free of networking/business logic.
- Use stable lazy-list keys and avoid allocating work during composition.
- Keep production UI and resources in `commonMain`. Test ViewModel transitions in `commonTest`; keep Android-only Compose instrumentation in `androidDeviceTest`.

Do not import `core:data`, Ktor, platform hosts or another feature.
