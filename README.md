# CoolBoost — Phone Cooling & Performance Booster

Production-ready Android app (Kotlin + Jetpack Compose + MVVM + Clean
Architecture). No root required, fully offline, no ads, no third-party
tracking.

## Honest disclaimer (built into the app's design)

No Android app can directly lower hardware/SoC temperature — there is no
public API for that, with or without root. What this app *does* do, using
only real, documented Android APIs:

- Reads battery temperature (`BatteryManager`) and, on Android 10+, the
  OS-level thermal status (`PowerManager.getCurrentThermalStatus()`) — the
  same signal the OS itself uses to decide when to throttle.
- Reduces heat sources: stops idle/background processes via
  `ActivityManager.killBackgroundProcesses()`, clears this app's own cache
  directories, and helps the user find apps to close manually.
- Predicts overheating trends with a transparent on-device linear model
  (no cloud, no telemetry).

This is presented in the UI as "cooling & performance optimization," not as
literal hardware refrigeration.

## Opening the project

1. Open Android Studio (Koala/Ladybug or newer recommended).
2. `File → Open`, select the `CoolBoost` folder.
3. Let Gradle sync (Android Studio will fetch the Gradle 8.7 wrapper distribution
   automatically on first sync since `gradle/wrapper/gradle-wrapper.properties`
   is included; an internet connection is required for the first sync only).
4. Run on a device or emulator running Android 7.0 (API 24) or newer.

## Architecture

```
app/
├── core/            App-wide constants, Result wrapper, manual DI container
├── domain/          Pure Kotlin: models, repository interfaces, use cases
├── data/             Room database, DataStore, repository implementations
├── monitoring/       Real device telemetry readers (thermal/RAM/CPU/FPS/battery)
├── cooling/          Smart & Extreme cooling engines
├── optimization/     Background app manager, cache cleaner, optimizer
├── ai/               Offline AI Optimization Engine (thermal prediction, RAM analysis, heavy-app detection)
├── analytics/        Performance history + usage tracking helpers
├── services/         Foreground monitoring/cooling services, boot receiver
├── workers/          WorkManager auto-clean / auto-optimize jobs
├── presentation/     ViewModels (MVVM)
├── ui/                Compose screens, theme (Material 3 + glassmorphism), components
└── utils/            Formatting, permissions, device helpers
```

Dependency injection is a small hand-rolled `AppContainer` (single
composition root) rather than Hilt/Dagger, so the project has zero extra
annotation-processing setup beyond Room's KSP compiler.

## Permissions requested

| Permission | Why |
|---|---|
| `POST_NOTIFICATIONS` | Show monitoring/alert notifications (Android 13+) |
| `PACKAGE_USAGE_STATS` (special, user-granted in Settings) | List recently active background apps for the Background App Manager |
| `FOREGROUND_SERVICE*` | Keep monitoring alive while backgrounded |
| `KILL_BACKGROUND_PROCESSES` | Standard, public "close background app" API |
| `RECEIVE_BOOT_COMPLETED` | Resume monitoring after reboot |

No root, no accessibility service, no device admin, no network permissions.

## Notes for further work

- FPS monitoring currently observes the app's own Compose UI thread via
  `Choreographer`; extending it to a system-wide overlay (e.g. for Game Mode)
  would require a `SYSTEM_ALERT_WINDOW` overlay measuring another app's
  render loop, which is out of scope for a no-root, Play-Store-safe app.
- Room schema is versioned at `1`; add a `Migration` before shipping schema
  changes instead of relying on `fallbackToDestructiveMigration()`.
