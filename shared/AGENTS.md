# Shared module instructions

This module is the Android/iOS composition root.

- Own root navigation, theme, feature ViewModel creation and data/domain wiring.
- Keep shared UI, navigation and resource access in `commonMain`.
- Limit `androidMain` and `iosMain` to unavoidable platform entry points.
- Depend on features and core modules; platform hosts depend only on this module.
- Resolve Gradle-generated configuration into typed data-layer values without logging secrets.

Validate changes with Android compilation plus the iOS device and simulator framework links.
