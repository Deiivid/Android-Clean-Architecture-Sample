# TDD — Rick and Morty catalog

Test Design Document for [SDD version 1.0](../specs/SDD-rick-and-morty-catalog.md). Development follows the test-first loop: reproduce or specify a failing behavior, implement the smallest change, then refactor while the suite remains green.

## Test strategy

1. Pure unit tests protect domain delegation and state transitions quickly.
2. Data tests protect request configuration, mapping and repository failure behavior.
3. Compose tests cover semantics/navigation that lower layers cannot prove.
4. A small emulator smoke test validates the real backend and platform integration.

No test may assert, snapshot or print a real credential value.

## Acceptance traceability

| Criterion | Evidence | Module | Current status |
|---|---|---|---|
| `AC-001` | Navigate all destinations; verify resource content | `app` + features | Manual smoke |
| `AC-002` | ViewModel loading/content tests; add empty/error cases | `feature:*` | Partial automation |
| `AC-003` | Failure followed by `retry()` requests page 1 again | `feature:*` | Planned |
| `AC-004` | Append next page and reject duplicate/end-of-list loads | `feature:*` | Characters automated; others partial |
| `AC-005` | Load-more failure preserves content and marks retry state | `feature:*` | Planned |
| `AC-006` | Present and blank credential header cases | `core:data` | Automated |
| `AC-007` | Header redaction review; Gradle tasks report names/sources only | `core:data` + build | Automated plus review |
| `AC-008` | Unit tests, lint and debug assembly | whole project | CI gate |

## Required test cases for new resource behavior

- Use case delegates the requested page and returns the repository `Result` unchanged.
- Mapper converts every backend field used by the domain model and pagination metadata.
- Repository wraps HTTP or mapping failures in `Result.failure`.
- ViewModel covers initial content, empty, error, retry, next page, duplicate next-page request, last page and load-more failure.
- Screen semantics expose retry/action labels and stable list identity.

## CI gate

```bash
./gradlew test lintDebug assembleDebug --warning-mode all
```

CI must also run `secretsStatus` to prove configuration wiring without requiring credentials on pull requests from forks.

## Emulator smoke checklist

- API 30: launch, navigate to all tabs, load images and request a second page.
- API 36: repeat navigation and pagination; verify edge-to-edge layout.
- Disable network: verify initial error and retry.
- Interrupt page 2: verify page 1 remains visible and retry succeeds.

## Evidence record

For each release or architectural change record the command/device, result, failing test if any and the affected `AC-*` identifiers. Never replace missing evidence with an assumption.
