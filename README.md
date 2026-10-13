# EmpManage

A Kotlin Multiplatform Mobile (KMM) employee management app built with Compose
Multiplatform for Android and iOS: profile CRUD, camera/gallery/document
integration, local persistence via Room KMP, and search/filter/sort backed by
documented-complexity data structures (HashSet duplicate index, min-heap top-N,
undo stack).

## Architecture

Clean Architecture with a strict inward dependency rule — `domain` has no
dependency on `data` or `presentation`:

```
composeApp/src/commonMain/kotlin/com/empmanage/
 ├─ domain/         model, repository interfaces, use cases, DSA utilities
 ├─ data/           Room entities/DAO, mappers, repository implementations
 ├─ presentation/   ViewModels, UI state, navigation
 ├─ ui/             Compose screens and components
 ├─ platform/       expect declarations (camera, gallery, file picker, permissions)
 └─ di/             Koin modules

composeApp/src/androidMain/   Android actuals (camera/picker/permissions), Koin android module
composeApp/src/iosMain/       iOS actuals, Koin iOS module
```

Full implementation plan, tech stack rationale, and phase breakdown live in the
project's planning document (see conversation history / shared doc link).

## Tech stack

- Kotlin Multiplatform + Compose Multiplatform
- Room KMP (KSP) for local persistence
- Koin for dependency injection
- Kotlin Coroutines + StateFlow, `combine()` for reactive list search/filter/sort

## Building

Android (works on any machine with the Android SDK):

```
./gradlew :composeApp:assembleDebug
```

iOS: `iosMain` sources compile as part of the KMP module, but linking a runnable
iOS app requires Xcode on macOS — see `iosApp/README.md`.

## Status

Phase 0 (project scaffolding) complete. See the planning document for the full
phase breakdown and current progress.
