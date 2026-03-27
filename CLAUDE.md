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

### Screens & Navigation

There is **no Compose Navigation / NavHost**. Screen transitions use a boolean `isSignedIn` state (`rememberSaveable`) in `MainActivity` that switches between two composables:
- `SignInScreen` — Google Sign-In OAuth flow via `rememberLauncherForActivityResult`
- `SheetScreen` — displays data fetched from Google Sheets API

### Data Layer

```
data/
  model/       SheetData.kt  — ValueRange (Moshi-annotated data class)
  remote/      SheetsApiService.kt (Retrofit interface), SheetsApiClient.kt (singleton OkHttp+Moshi)
  repository/  SheetsRepository.kt — fetches OAuth token via GoogleAuthUtil, calls Sheets REST API
```

- **Network**: Retrofit 2 + OkHttp 4 + Moshi. Base URL: `https://sheets.googleapis.com/`. Logging interceptor set to `BODY` level.
- **Auth**: Google Sign-In (`play-services-auth`). The access token for the Sheets API is retrieved with `GoogleAuthUtil.getToken()` on `Dispatchers.IO`.
- **No DI framework**: All dependencies are instantiated manually. `SheetsApiClient` is a Kotlin `object` (singleton). `SheetsRepository` is created directly inside `SheetViewModel`.

### ViewModel / UI State

`SheetViewModel` (extends `AndroidViewModel`) uses a sealed interface:

```kotlin
sealed interface SheetUiState { Idle | Loading | Success(data) | Error(message) }
```

State is exposed as `StateFlow` and collected in Compose with `collectAsStateWithLifecycle()`.

## Key Conventions

- Dependency versions are managed via the version catalog at `gradle/libs.versions.toml`.
- The app uses Kotlin DSL for all Gradle scripts (`*.gradle.kts`).
- Composable previews use `@Preview(showBackground = true)` with the app theme wrapper.
