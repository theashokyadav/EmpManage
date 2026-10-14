# EmpManage

A Kotlin Multiplatform Mobile (KMM) employee management app built with Compose
Multiplatform for Android and iOS: a validated employee form, camera/gallery/
document integration, local persistence via Room KMP, and a searchable/
filterable/sortable employee list backed by documented-complexity data
structures (HashSet duplicate index, a hand-rolled min-heap for top-N salary,
an ArrayDeque undo stack, and a prefix trie for name search).

## Status

All core sections (1–6) from the spec are implemented and verified against
real Android builds, plus every bonus item: unit tests (53, well past the
15-test bar), Koin DI, dark mode, pagination, and animations/shared-element
transitions. iOS sources are written throughout but unverified — there's no
Xcode toolchain in this dev environment; see [iOS status](#ios-status) below.

## Features

- **Employee Form** — all spec'd fields (name, email, phone, address, gender,
  department, skills, employment type, active switch, joining date, salary)
  with real-time validation, errors shown on blur, Submit gated on full
  validity, and inline duplicate-detection errors on Email/Phone/Full Name.
- **Profile photo & resume** — camera/gallery bottom sheet with Android
  runtime permissions, images stored as local file paths; resume upload
  (PDF/DOC/DOCX) with a 5 MB size limit enforced via a snackbar.
- **Employee list** — always-visible search (name/email/department, with
  match highlighting), a filter bottom sheet (department/status/employment
  type, AND-composed, with an active-filter-count badge), a sort menu
  (name/joining date/salary, both directions), and pagination in pages of 20.
- **Card interactions** — long-press context menu (Edit/Delete/View Details),
  delete confirmation dialog, and a 5-second undo-delete snackbar.
- **Top Earners tab** — the top N highest-paid employees via a min-heap,
  with a stepper to change N (1–10) reactively.
- **Dark mode** — follows the system setting by default; a toggle persists an
  explicit override in a cross-platform DataStore.

## Architecture

Clean Architecture with a strict inward dependency rule — `domain` has no
dependency on `data` or `presentation`:

```
composeApp/src/commonMain/kotlin/com/empmanage/
 ├─ domain/
 │   ├─ model/        Employee, Gender/Department/EmploymentType/Skill enums
 │   ├─ repository/   EmployeeRepository, ThemePreferenceRepository (interfaces)
 │   ├─ usecase/      Add/Update/Delete/UndoDelete/GetEmployees, TopNSalaryUseCase
 │   └─ util/         FormValidators, PhoneNormalizer, DuplicateIndex, NameIndex,
 │                     MinHeap, UndoStack, NameTrie — each with a KDoc complexity note
 ├─ data/
 │   ├─ local/db/      Room entity/DAO/database (expect/actual RoomDatabaseConstructor)
 │   ├─ local/mapper/  Entity <-> domain model mapping
 │   ├─ local/datastore/ Cross-platform DataStore<Preferences> (Okio-backed)
 │   └─ repository/    EmployeeRepositoryImpl, ThemePreferenceRepositoryImpl
 ├─ presentation/
 │   ├─ viewmodel/     EmployeeForm/List/Detail/TopEarners/ThemeViewModel
 │   ├─ state/         Immutable UI state classes (FieldState<T>, FilterState, ...)
 │   └─ navigation/    Screen (sealed class: List/Form/Detail)
 ├─ ui/                Compose screens + reusable components
 ├─ platform/          expect declarations: camera/gallery/document pickers,
 │                     image decoding, DB builder, DataStore path, clock
 └─ di/                Koin modules (domain/data/viewModel)

composeApp/src/androidMain/   Android actuals (verified against real builds)
composeApp/src/iosMain/       iOS actuals (written, unverified — see below)
```

Every DAO call runs on `Dispatchers.IO`; every list transformation (search,
filter, sort, pagination) happens in `EmployeeListViewModel` via `combine()`
on top of `GetEmployeesUseCase()`'s `Flow` — Composables only render the
resulting `EmployeeListUiState`.

### How pagination actually works

`EmployeeListViewModel` pages at the DAO level: `ObserveEmployeePageUseCase`
calls one of `EmployeeDao`'s six `ORDER BY ... LIMIT :limit` queries (one per
`SortOption`, since Room/SQLite can't parameterize `ORDER BY` via a bind
parameter), and widens `limit` by 20 (`visibleCountState`) as the user
scrolls or taps "load more" — a real re-query every time, driven by
`flatMapLatest`, not a client-side slice of an already-fetched list.
`observeCount()` tracks the true total row count so `hasMore` reflects the
DB, not just what's in memory.

Search and department/status/employment-type filtering are still applied
client-side on top of that loaded window, because Section 4's search needs
to scan the *whole* matching set to be correct, and SQLite can't easily
combine a dynamic multi-column substring search with multi-value filters in
one static `@Query`. Changing search/filter/sort resets the window back to
one page (20) rather than pretending the old page count still matches.

## Tech stack

- Kotlin Multiplatform 2.1 + Compose Multiplatform 1.7
- Room KMP 2.7 (KSP) — the `RoomDatabaseConstructor` expect/actual pattern,
  confirmed working against a real Android KSP run
- Koin 4 (including `koin-compose-viewmodel`)
- Kotlin Coroutines + StateFlow/Flow (`combine`, `debounce`, `stateIn`)
- kotlinx-datetime (date formatting) and DataStore + Okio (theme persistence)

## Building

Android (works on any machine with the Android SDK):

```
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:testDebugUnitTest
```

### iOS status

`iosMain` actuals exist for every platform integration point (camera/
gallery/document pickers, image decoding, the Room database builder, the
DataStore path, the KMP clock) and compile as part of the shared module's
Kotlin/Native metadata compilation. They are **not** compiled into a running
iOS binary or verified on-device, because this dev environment has no Mac/
Xcode toolchain. `iosApp/README.md` has the steps to finish wiring an Xcode
project once that access exists — the Swift entry point sources
(`iOSApp.swift`, `ContentView.swift`) are already written and ready to drop
in.

## Testing

53 unit tests in `commonTest`, covering the DSA core (duplicate detection,
min-heap top-N, undo stack, name trie), phone normalization, every form
field validator, and small UI-adjacent helpers (ordinal formatting):

```
./gradlew :composeApp:testDebugUnitTest
```

## Development notes

This project was built with Claude Code as a pair-programming tool —
accelerating boilerplate, surfacing edge cases, and speeding up iteration.
All architecture decisions (Clean Architecture layering, the DSA choices in
`domain/util`, the DAO-level pagination design), debugging, and review were
done by me

