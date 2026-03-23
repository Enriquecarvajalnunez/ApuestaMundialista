# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

All Gradle commands are run from the project root. On Windows, use `./gradlew.bat`; on Linux/macOS use `./gradlew`.

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew :app:testDebugUnitTest --tests "com.example.pollafutbolera_android.ExampleUnitTest"

# Run instrumented (on-device) tests
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Install debug build on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

## Project Architecture

**Single-module Android app** using Jetpack Compose + Material3.

- **Package**: `com.example.pollafutbolera_android`
- **Min SDK**: 24 / **Target SDK**: 36
- **Language**: Kotlin
- **UI framework**: Jetpack Compose with Material3
- **Theme**: `PollaFutbolera_AndroidTheme` in `ui/theme/` — supports dynamic color (Android 12+), dark/light modes. Color tokens are in `Color.kt`, typography in `Type.kt`.
- **Entry point**: `MainActivity` — single-activity architecture. Uses `enableEdgeToEdge()` and `Scaffold` as the root layout.

## Key Conventions

- Dependency versions are managed via the version catalog at `gradle/libs.versions.toml`.
- The app uses Kotlin DSL for all Gradle scripts (`*.gradle.kts`).
- Composable previews use `@Preview(showBackground = true)` with the app theme wrapper.
