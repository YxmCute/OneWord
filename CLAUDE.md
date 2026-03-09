# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

OneWord is a Kotlin Multiplatform + Compose Multiplatform poetry app targeting Android, iOS, and macOS. The shared business logic, UI, networking, and persistence live in a single KMP module (`composeApp`), with thin platform-specific entry points for each target.

Key technologies:
- Kotlin Multiplatform (Android, iOS, Desktop JVM)
- Compose Multiplatform (common UI)
- Ktor Client (HTTP)
- SQLDelight (SQLite persistence)
- Kotlinx Serialization
- XcodeGen (iOS container project generation)

Upstream documentation, release notes, and install guides are under `docs/` — prefer aligning with those conventions when modifying behavior or scripts.

## Repository & module structure

High-level layout:
- `composeApp/`
  - Single KMP module containing shared UI, state, and data layers
  - Android and Desktop JVM entry points
- `iosApp/`
  - iOS container Xcode project definition generated from `project.yml`
- `scripts/`
  - Shell helpers for iOS/Xcode workflows (simulator, IPA export, project regeneration)

Gradle setup:
- Root project: `settings.gradle.kts` defines `OneWord` with a single included module `:composeApp`.
- Root `build.gradle.kts` exposes plugin versions via the version catalog and does not contain per-target config.
- `composeApp/build.gradle.kts` configures all KMP targets and dependencies and should be treated as the main build configuration file.

When changing build behavior, prefer editing `composeApp/build.gradle.kts` and keeping the root build file minimal.

## Architecture

### Entry points and app root

Platform launchers:
- Android: `composeApp/src/androidMain/kotlin/com/koma/oneword/MainActivity.kt`
  - `ComponentActivity` that enables edge-to-edge (`WindowCompat.setDecorFitsSystemWindows(window, false)`) and mounts the shared `OneWordApp` composable with `AppInsetMode.IMMERSIVE_TOP`.
- Desktop: `composeApp/src/desktopMain/kotlin/com/koma/oneword/Main.kt`
  - Uses `application { Window { ... } }` to set up the main window, menu bar, keyboard shortcuts, and hosts a desktop-specific version of the app shell (`DesktopApp`) that mirrors `OneWordApp` but with desktop menus.
- iOS: `composeApp/src/iosMain/kotlin/com/koma/oneword/MainViewController.kt`
  - Exposes `MainViewController(): UIViewController` wrapping `OneWordApp()` via `ComposeUIViewController` for integration into the Xcode-generated container project.

Shared app root:
- `composeApp/src/commonMain/kotlin/com/koma/oneword/App.kt`
  - `OneWordApp` is the root composable used by all platforms.
  - Responsibilities:
    - Own an `AppContainer` lifecycle and dispose it when the app leaves composition.
    - Instantiate `HomeViewModel` and `SettingsViewModel` from the container.
    - Hold lightweight navigation state between HOME and SETTINGS screens.
    - Mount the theme system (`OneWordTheme`) using settings from the `SettingsViewModel`.
    - Control the global theme picker overlay and propagate callbacks to the view models.

When adding new global UI (e.g., overlays, navigation destinations), prefer wiring it through `OneWordApp` rather than platform-specific entry points unless it is truly platform-only.

### Dependency & state management

Application container:
- `composeApp/src/commonMain/kotlin/com/koma/oneword/app/AppContainer.kt`
- `composeApp/src/commonMain/kotlin/com/koma/oneword/app/AppContainerFactory.kt`
  - Provide a shared place to construct long-lived objects: repositories, Ktor client, SQLDelight drivers, coroutine scope, logging.
  - `AppContainerFactory.rememberAppContainer()` is the canonical way to obtain an `AppContainer` inside Composables on all platforms.
  - `AppContainer.close()` should be called when the app is disposed (already handled in `OneWordApp` and `DesktopApp`).

Repositories & data sources:
- Networking API wrapper: `composeApp/src/commonMain/kotlin/com/koma/oneword/data/api/PoetryApi.kt`
  - Encapsulates HTTP calls to the “今日诗词” service (token + sentence endpoints) using Ktor.
- Poetry repository: `composeApp/src/commonMain/kotlin/com/koma/oneword/data/repository/PoetryRepository.kt`
  - Orchestrates fetching poetry from the network, caching into SQLDelight, and supplying domain models.
- Settings repository: `composeApp/src/commonMain/kotlin/com/koma/oneword/data/repository/SettingsRepository.kt`
  - Stores and retrieves theme-related preferences (mode, seed color, etc.) via SQLDelight.

View models:
- `composeApp/src/commonMain/kotlin/com/koma/oneword/presentation/HomeViewModel.kt`
- `composeApp/src/commonMain/kotlin/com/koma/oneword/presentation/SettingsViewModel.kt`
  - Manage UI state flows for the home and settings screens using coroutine `scope` from `AppContainer`.
  - Expose intent-style methods (e.g., `load`, `refresh`, `toggleExpand`, `openThemePicker`, `updateThemeMode`, `updateThemeSeed`).

When introducing new screens or behaviors, follow the existing pattern:
1. Add/extend a repository in `data/repository/` or API wrapper in `data/api/`.
2. Add/update a view model in `presentation/` exposing state via `StateFlow`.
3. Render it in a composable screen under `ui/...` and hook it into `OneWordApp` / `DesktopApp`.

### UI & theming

Key UI modules (commonMain):
- Theme system:
  - `theme/AppTheme.kt` — defines `OneWordTheme` and theme-related state.
  - `theme/ThemeColorEngine.kt`, `theme/ColorUtils.kt` — derive dynamic palettes from a seed color.
  - `model/AppThemeSettings.kt`, `model/ThemeMode.kt` — represent current theme configuration.
- Screens & components:
  - Home: `ui/home/HomeScreen.kt` — composes the main poetry view, refresh actions, expand/collapse, and error handling.
  - Settings: `ui/settings/SettingsScreen.kt` — UI for selecting theme mode and opening the theme picker.
  - Shared components: `ui/components/PosterBackground.kt`, `ui/components/ThemePickerOverlay.kt` — background visuals and theme picker overlay.
- Platform abstractions:
  - `ui/PlatformBlurSurface.kt` — platform-specific blur implementation.
  - `ui/AppInsetMode.kt` — enum describing how the app should treat system insets; passed from platform entry points to `OneWordApp`.

Prefer placing reusable Composables under `ui/components/` and screen-level layouts under `ui/home` or `ui/settings` (or new `ui/<feature>` packages).

### Utilities

Utility abstractions in `commonMain` provide platform-independent services:
- `util/PlatformLogger.kt` — logging facade.
- `util/PlatformTime.kt` — time utilities.

Platform-specific implementations live under the respective source sets (e.g., `androidMain`, `iosMain`, `desktopMain`). When you need new cross-platform utilities, define expect/actual pairs in these utility files.

### Persistence & SQLDelight

SQLDelight configuration:
- Defined in `composeApp/build.gradle.kts` (`sqldelight { databases { create("OneWordDatabase") { ... } } }`).
- Database package: `com.koma.oneword.database`.
- Migration verification is enabled (`verifyMigrations.set(true)`), so make sure to provide migrations when altering schemas.

Database access occurs through repositories; avoid accessing generated SQLDelight APIs directly from UI code.

## Common commands

All commands are intended to be run from the repository root.

### Gradle builds

Android debug APK:
```bash
./gradlew :composeApp:assembleDebug
```
Output: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

macOS Desktop DMG:
```bash
./gradlew :composeApp:packageDmg
```
Output: `composeApp/build/compose/binaries/main/dmg/OneWord-1.0.0.dmg`

### Tests

Known verified test commands (from README):
```bash
./gradlew :composeApp:desktopTest
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:packageDmg
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

To run only desktop tests:
```bash
./gradlew :composeApp:desktopTest
```

Kotlin/Gradle naming is standard; if you add new test targets or tasks, keep them under the `composeApp` project and follow the `:composeApp:<target>Test` naming convention when possible.

### iOS workflows

The iOS container project is defined under `iosApp/project.yml` and generated via XcodeGen.

Regenerate the iOS Xcode project:
```bash
cd iosApp
xcodegen generate
```

Preferred iOS workflows (from `README.md`):

- Reset simulator state and run app in iOS Simulator:
  ```bash
  ./scripts/reset_and_run_ios.sh
  ```

- Open Xcode project fresh (regenerate + clear `DerivedData`):
  ```bash
  ./scripts/open_ios_fresh.sh
  ```

- Build and run the iOS Simulator target from the command line:
  ```bash
  ./scripts/run_ios_simulator.sh
  ```

- Build and export a development IPA (requires configured Apple Developer Team):
  ```bash
  TEAM_ID=YOUR_TEAM_ID BUNDLE_ID=com.koma.oneword.iosApp ./scripts/build_ios_ipa.sh
  ```

You can override variables like `DEVICE_ID`, `CONFIGURATION`, `EXPORT_METHOD`, `ARCHIVE_PATH`, and `EXPORT_PATH` as needed; see `README.md` for details.

## Development notes for Claude

- Prefer keeping cross-platform logic in `commonMain` and limit platform-specific code to `androidMain`, `iosMain`, and `desktopMain` when platform APIs are required.
- When adding new features, follow the existing layering: API/DB → repository → view model → UI composable.
- When you need theme or setting information, read/write via `SettingsRepository` and the existing view models instead of introducing new global singletons.
- Be careful when editing `composeApp/build.gradle.kts`; changes there affect all three platforms.
- SQLDelight migrations must pass `verifyMigrations`; ensure migrations are added and updated when schemas change.
