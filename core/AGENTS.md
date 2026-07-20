# Core modules instructions

Apply the dependency rule strictly inside `core`:

- `model`: immutable business entities only; no Android, Retrofit, Hilt or UI types.
- `domain`: repository interfaces and focused use cases; depend only on `model`.
- `data`: DTOs, mappers, Retrofit/OkHttp, repository implementations and data-layer DI. Translate failures at this boundary and never leak DTOs outward.
- `testing`: reusable test infrastructure only; production modules must not depend on it.

Repository interfaces belong in domain and implementations belong in data. Mapping between remote and domain models must be explicit and testable. Credentials must be optional at the HTTP boundary and every credential header must be redacted.

Prefer pure JVM tests for model/domain and focused unit or MockWebServer tests for data.
