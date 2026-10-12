# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

"Моё Хозяйство 2" (`FermaCompose2`, applicationId `com.zaroslikov.fermacompose2`) — Android app for managing small farm holdings. Two top-level project kinds: **Ферма** (farm: production/sales/expenses/write-offs/animals/warehouse/finance) and **Инкубатор** (incubation tracking with day counters and bookmarks). Offline-first Room database, Jetpack Compose UI, daily WorkManager notifications, Yandex Mobile Ads + AppMetrica, RuStore in-app updates, QR-code sharing of templates.

UI text and code comments are in Russian; keep new user-facing strings in Russian.

## Build & run

Gradle wrapper, JDK 17 (`domain` is JVM 11). Use `./gradlew` from Bash or `.\gradlew.bat` from PowerShell.

```bash
./gradlew assembleDebug            # debug APK
./gradlew installDebug             # build + install on device/emulator
./gradlew assembleRelease          # signed release (needs keystore.properties, see below)
./gradlew clean
./gradlew :app:compileDebugKotlin  # fast compile check after edits
./gradlew test                     # JVM unit tests (all modules)
./gradlew :app:testDebugUnitTest --tests "com.zaroslikov.fermacompose2.ExampleUnitTest"
./gradlew connectedAndroidTest     # instrumented tests (device required)
```

There is no lint/format task wired up beyond the AGP defaults (`./gradlew lint`). The test suites are placeholder templates only — there is no meaningful test coverage in this repo.

**`keystore.properties` is required to configure the build at all.** `app/build.gradle.kts` reads it unconditionally at configuration time, but it is gitignored. Without a `keystore.properties` at the repo root (keys: `keyAlias`, `keyPassword`, `storeFile`, `storePassword`) *every* Gradle invocation fails, including debug builds and tests.

Release builds enable R8 (`isMinifyEnabled`/`isShrinkResources`); check `app/proguard-rules.pro` when adding reflection-based or serialized types.

AppMetrica is disabled for debug via `isDebugBuild()` in `app/build.gradle.kts`, which inspects whether the requested task names contain "Debug" — invoking a build through a task name without that substring will enable analytics.

## Module layout

Four Gradle modules with a strict dependency direction:

- **`:domain`** — pure Kotlin/JVM library (no Android). Holds `Domain*` models (`models/table`, `models/dto`, `models/enums`) and **repository interfaces** (`repository/`). Everything above depends on this; it depends on nothing but coroutines + kotlinx-serialization.
- **`:data`** — Android library. Room implementation only: `room/table` entities, `room/dao`, `room/dto` (query projections), `room/converter` (TypeConverters), `room/repository` (`*RepositoryImpl` implementing the domain interfaces), `room/mapper` (extension functions `toDomain*` / `to*RoomMap` translating Room ↔ domain types), plus the Hilt modules `di/DatabaseModule` and `di/RoomRepositoryModule`.
- **`:crm`** — Android library (`com.zaroslikov.crm`) with the self-contained CRM feature: its **own Room database `crm_database`** (`data/room/database/CrmDatabase.kt`, clients/contacts/interactions/tasks, project link by plain `idPT` column — no cross-DB FK), own domain models/repositories, own Hilt modules (`di/`), Compose screens (`ui/dashboard`, `ui/clients`, `ui/client_card`, `ui/client_edit`, `ui/tasks`) and a tiny MVI base (`ui/base/CrmBaseViewModel`). Depends only on `:domain`; **never on `:app` or `:data`**, so it carries its own UI primitives (`ui/components`, `ui/theme`) mirroring the app palette. Clients are synced from `sale_table.buyer` through `CrmSalesRepository` (interface in `:domain`, impl over `CrmSaleDao` in `:data`); the match key is the immutable `sales_key` (`TRIM(buyer)`), the display name is editable. Deleting a client is a soft archive (`is_archived`) so a re-sync does not resurrect it. `:app` plugs it in via `crmNavGraph(navController)` in `ProjectNavGraph` (entry route `crm/{itemIdPT}`, opened from the finance screen button) and includes its tables in the JSON backup (`BackupData.crm`), DB wipe (`clearAll`) and project deletion (`deleteForProject`).
- **`:app`** — Compose UI, ViewModels, navigation, workers, ads/metrics.

**ViewModels inject domain repository interfaces, never DAOs or Room entities.** Room types never leave `:data` — every DAO result is mapped to a `Domain*` type in the repository impl. When adding a query you normally touch four files: DAO + (optionally) a `*Dto`, a mapper, the domain model, and the repository interface/impl pair.

`domain/main/` (untracked, next to the real `domain/src/main/`) is a stray leftover directory, not a source set.

## Database

`data/room/database/AppDatabase.kt` — currently **version 16**, `exportSchema = false`, DB file name `item_database`. Migrations live in `data/room/database/migration/Migration_N_M.kt` and must be registered in `DatabaseModule.provideDatabase`. The builder deliberately has no `.fallbackToDestructiveMigration()` — a missing migration must crash rather than silently wipe the user's farm.

**Никогда не сохраняй `R.drawable.*` (и любой другой id ресурса) в БД, бэкап или QR.** aapt2 раздаёт их заново на каждой сборке, и они сдвигаются при изменении набора ресурсов приложения или библиотек — так 3.1.1 ловила `Resources$NotFoundException` на стартовом экране. Иконки хранятся как `AppIcon` (`domain/models/enums/AppIcon.kt`) со стабильными кодами; в ресурс это превращается только в `ui/elements/icon/AppIconRes.kt`.

**Переработка** (тип списания «В переработку»): `recycling_table` — партия переработки, сырьё — строки `write_off_table` с `recycling_id` (без FK, удаляются в `RecyclingDao.deleteRecycling`). Готовая продукция попадает в `add_table` только при завершении (`add_id`), поэтому «замороженная» продукция не видна на складе и в учёте без фильтров в остальных запросах. Созревшие переработки завершает `RecyclingRepository.completeDueRecycling` при открытии экранов списаний, продукции, склада и финансов. Сырьё переработки исключено из аналитики собственных нужд и утилизации (`recycling_id IS NULL`).

`DatabaseModule` also seeds `app_settings_table` in `onCreate` with a generated `device_id` and a `current_version_app` string; bump that literal when it needs to track a release.

Every DAO is `@Provides`-ed individually in `DatabaseModule`, and every repository is `@Binds`-ed in `RoomRepositoryModule` — new repositories require entries in both.

## UI architecture (MVI)

Screens follow a per-feature package under `ui/…` containing `XIntent.kt`, `XState.kt`, `XReduce.kt`, `XScreen.kt`, `XViewModel.kt`.

- `base/state/` — `BaseState` (isLoading, navigate) → `ListState` / `EntryState` / `SectionState`. `SectionState` is the big shared contract for the farm "sections" (add/sale/expenses/write-off): search, group mode, archive flag, bottom-sheet state, templates, QR state, settings.
- `base/intent/` — marker `BaseIntent` plus cross-cutting intent families `TemplateIntent`, `QrCodeIntent`, `EntryIntent` handled by the base ViewModels.
- `base/reduce/` — `BaseReducer.reducer(state, intent): STATE` (pure state transition), `SectionReducer` for section screens.
- `base/viewModel/` — `BaseViewModel<STATE, INTENT>` owns `_state: MutableStateFlow`, a `_events: SharedFlow<EventFile>` (file/URI results), a `_navigation: SharedFlow<UiEvent>`, and `showMessage()` → global `SnackbarController`. Subclasses `ListViewModel`, `EntryViewModel`, `EntryNewViewModel`/`2`/`3` layer on shared behavior; `EntryNewViewModel3` is the current one for section screens and already implements template CRUD, QR encode/decode and pinning, leaving `loadData/insert/update/delete/onIntent/onQrCodeIntent` abstract.

Screens dispatch through a single `onIntent(intent)`; the ViewModel calls `sendIntent(intent)` (reducer updates state) and then side-effects in a `when`. Compose collects `state` via `collectAsStateWithLifecycle()`.

Shared section chrome (top bar with search + group toggle, FABs, template/QR bottom sheets) lives in `ui/project/sections/baseComposable/BaseSectionScreen.kt` — reuse it rather than rebuilding a Scaffold for a new section.

Reusable UI lives in `ui/elements/` (dialogs, bottom sheets, text fields, empty-list states, cards); formatting/number/date/chart helpers in `supportFun/`; injectable Android-facing helpers (`ResourceProvider`, `QrGenerator`, `ImageGeneration`, `QrNavigationManager`, `SnackbarController`) in `utils/`, bound in `app/di/uiModule/UiModule.kt`.

## Navigation

String-route Navigation-Compose with **nested NavHosts**:

- `InventoryNavHost` (root) — first screen, profile/settings/about, and the two project hubs.
- `ProjectNavHost` — everything inside a farm project, started from `MainProjectScreen`; receives `rootNavController` for escaping to root destinations.
- `IncubatorNavGraph` — incubator project internals.

Each screen exposes an `object XDestination : NavigationDestination` with `route`, `titleRes`, arg-name constants and `routeWithArgs`. Build routes with the `nav(...)` / `navNull(...)` helpers in `ui/navigation/supportNav.kt` rather than string-concatenating by hand. Arguments are read in the ViewModel from `SavedStateHandle` (e.g. `savedStateHandle[HomeDestination.itemIdArg]`), not passed down as composable parameters.

`InventoryApp.kt` resolves the start destination asynchronously (`viewModel.resolveStartDestination(intent)`) — it handles the `myferma://template` deep link used by QR template sharing, and gates the nav host behind the first-launch training screen, the post-update screen and the app-open ad.

## Resources gotcha

`strings.xml`, `themes.xml`, `colors.xml`, `dimens.xml` live in **`app/src/main/res/values-v23/`**, not `values/` (only `ic_launcher_background.xml` is in `values/`). minSdk is 26, so the qualifier always applies. Add new strings to `values-v23/strings.xml`.

## Background work & third-party SDKs

- WorkManager with Hilt (`@HiltWorker`); `InventoryApplication` is a `Configuration.Provider` using `HiltWorkerFactory`. Scheduling goes through `data/worker/WorkManagerRepository` (`IncubatorWorker`, `ProjectWorker`), enqueued as unique one-time work keyed `incubator_<bookmarkId>_<time>` / project equivalent, re-armed after each fire.
- Yandex Mobile Ads: initialized in `InventoryApplication`; app-open ad in `MainActivity.SplashScreen`, banners in `ui/ads/Banner.kt`. Ad display is suppressed for debug builds via `BuildConfig.BUILD_TYPE`.
- AppMetrica events go through `supportFun/YandexMetricRepository`.
- RuStore `appupdate` SDK drives the in-app update flow surfaced by `LoadUpdate` and class `InventoryAppViewModel` — which lives in the misspelled file `InvertoryAppViewModel.kt`.
- QR: ZXing for encoding, ML Kit barcode-scanning + CameraX for scanning (`ui/elements/bottomSheet/QrScannerScreen.kt`).