# iOS host instructions

This directory is the thin SwiftUI host for the shared Compose application.

- Keep Swift code limited to app lifecycle and hosting `MainViewController`.
- Do not duplicate navigation, screens, data access or business behavior in Swift.
- Keep the Gradle framework build phase before Swift compilation and User Script Sandboxing disabled.
- Do not add signing identities, team IDs or credentials to source control.

Validate with an unsigned arm64 simulator build.
