# Feature modules instructions

Each feature owns one vertical presentation slice.

- Depend only on `core:domain`, `core:model` and UI libraries.
- A route obtains the ViewModel and collects lifecycle-aware state; a screen renders state and emits callbacks.
- Expose immutable `StateFlow` and model loading, content, empty and error states explicitly.
- Keep pagination idempotent: prevent concurrent loads, append successful pages and preserve existing content on load-more failure.
- Keep composables small, resource-backed, previewable and free of networking/business logic.
- Use stable lazy-list keys and avoid allocating work during composition.
- Test ViewModel state transitions with coroutine test dispatchers; add Compose tests for behavior that cannot be proven below the UI layer.

Do not import `core:data`, Retrofit, OkHttp, `BuildConfig`, activities or another feature.
