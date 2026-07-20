# Secrets on Android

This project keeps development credentials out of source control. Gradle resolves environment variables first, then Gradle properties, then ignored `secrets.properties`. It generates `BuildConfig` fields, the app maps them to typed configuration, and Hilt injects credentials into `CredentialsInterceptor`.

```text
CI environment / Gradle properties / secrets.properties
        ↓
BuildConfig
        ↓
AppConfigurationModule
        ↓
ApiCredentials
        ↓
CredentialsInterceptor → redacted HTTP headers
```

## Setup

1. Copy `secrets.properties.example` to `secrets.properties`, or export the same names as environment variables.
2. Fill only the values required by the target API.
3. Never commit `secrets.properties`.
4. Inspect sources safely with `./gradlew secretsStatus`; values are never printed.

The public base URL resolves from `RICK_AND_MORTY_BASE_URL` as environment variable → Gradle property → safe public default.

## Configuration contract

| Name | GitHub type | Local source | Required | Consumer |
|---|---|---|---|---|
| `RICK_AND_MORTY_BASE_URL` | Variable | Environment or Gradle property | No; public default | Retrofit base URL |
| `RICK_AND_MORTY_API_KEY` | Secret | Environment, Gradle property or `secrets.properties` | No | `X-Api-Key` |
| `DEMO_CLIENT_ID` | Variable | Environment, Gradle property or `secrets.properties` | No | `X-Client-Id` |
| `DEMO_TENANT_ID` | Variable | Environment, Gradle property or `secrets.properties` | No | `X-Tenant-Id` |
| `DEMO_ACCESS_TOKEN` | Secret | Environment, Gradle property or `secrets.properties` | No | `Authorization: Bearer …` |

Blank values are omitted. OkHttp logging explicitly redacts every credential header.

## Gradle tasks

```bash
# Show only configured/missing and the source; never values.
./gradlew secretsStatus

# Persist supported environment variables into ignored secrets.properties.
./gradlew importSecretsFromEnvironment

# Strict demo check; intentionally not attached to normal builds.
./gradlew verifyEnvironmentVariables
```

The import uses a temporary file, replacement move and owner-only permissions when the filesystem supports POSIX permissions.

## GitHub Actions

The CI workflow maps non-sensitive `${{ vars.* }}` values and sensitive `${{ secrets.* }}` values into the same environment-variable contract. Missing credentials do not break public-API builds or pull requests from forks.

Create the public repository variable with:

```bash
gh variable set RICK_AND_MORTY_BASE_URL --body "https://rickandmortyapi.com/api/"
```

Use `gh secret set NAME` for sensitive values so the CLI prompts for the value rather than putting it in shell history. Workflow references do not create repository variables or secrets by themselves.

## Important limitation

An Android APK runs on a user-controlled device. Any value compiled into `BuildConfig`, resources, native code or an obfuscation layer can eventually be extracted. This pattern prevents accidental Git exposure; it does not turn a client-side value into a true secret.

Long-lived private keys, service-account credentials and OAuth client secrets must stay on a trusted backend. Mobile clients should receive short-lived, scoped tokens after authentication and use platform-backed storage only for protecting tokens at rest.
