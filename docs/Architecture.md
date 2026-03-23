# Terra — Architecture

## Overview

Terra follows **Clean Architecture** with **MVVM**, enforced through a strict multi-module Gradle setup. Each layer has a defined direction of dependency; inner layers never know about outer ones.

```
Presentation  ──▶  Domain  ◀──  Data  ──▶  Network
(UI / VM)         (Models)      (Repo)
```

---

## Module Graph

```
app/
├── feature:home          ──▶  core:domain:country
├── feature:details       ──▶  core:domain:country
├── feature:welcome       ──▶  (standalone)
├── core:data:country     ──▶  core:domain:country
│                         ──▶  core:domain:common
│                         ──▶  core:network
├── core:domain:country   ──▶  core:domain:common
├── core:domain:common    ──▶  (standalone)
├── core:network          ──▶  (standalone)
└── core:navigation       ──▶  (standalone)
```

**Rules:**
- Feature modules must NOT depend on each other.
- Domain modules must NOT depend on data or network modules.
- `app` owns the Hilt component; it depends on all feature modules and `core:data:country` (to keep `DataModule` on the Hilt graph).

---

## Layers

### Presentation (`feature/*`)

- Jetpack Compose screens and `@HiltViewModel` ViewModels.
- ViewModels expose a single `StateFlow<UiState>` via `stateIn(WhileSubscribed(5_000))`.
- No business logic — ViewModels call use cases and translate `Result` into `UiState`.
- Errors are mapped from `DomainError` → `UiError` via `UiErrorMapper`; `UiState.Error` carries a `UiError`. Both types are defined in the feature's contract. Screens render `UiError` directly — no error formatting in ViewModels.
- Navigation is triggered by emitting `NavigationEvent` to the `Navigator` singleton; ViewModels never touch `NavController`.

#### Feature Contract

Every feature defines a **contract** — a single file that declares the three building blocks of its presentation layer:

| Type | Role |
|---|---|
| `UiError` | Sealed interface of user-visible failure cases (`Offline`, `Timeout`, `SessionExpired`, `NotFound`, `Generic`). Carried by `UiState.Error`. |
| `UiState` | Sealed interface representing every possible screen state (`Loading`, `Success`, `Error`, …). Exposed as `StateFlow`. |
| `UiEvent` | Sealed interface of user-driven inputs the screen can send to the ViewModel (button taps, text changes, etc.). |
| `SideEffect` | Sealed interface of one-time effects the ViewModel sends to the screen (show snackbar, trigger scroll, etc.). Not to be used for navigation — emit a `NavigationEvent` to `Navigator` instead. |

```kotlin
// feature/home/presentation/HomeContract.kt
object HomeContract {
    sealed interface UiError { ... }
    sealed interface UiState { ... }
    sealed interface UiEvent { ... }
    sealed interface SideEffect { ... }
}
```

The contract file lives in the `presentation` package alongside `UiErrorMapper` and the navigation interface — no separate sub-package. It is the single source of truth for all presentation-boundary types. Screens, ViewModels, and `UiErrorMapper` reference these types exclusively — no ad-hoc type definitions elsewhere in the feature.

### Domain (`core/domain/*`)

Two sub-modules:

| Module | Contents |
|---|---|
| `core:domain:common` | `DomainError`, `Result` |
| `core:domain:country` | `Country` model (+ nested models), `CountryRepository` interface, use case interfaces + impls, `CountryUseCaseModule` |

Use cases are the entry point into the domain from the presentation layer. Trivial single-repository pass-throughs (no added orchestration or business logic) may be skipped — the ViewModel may call the repository directly. Use cases earn their place when they combine repositories, enforce rules, or apply domain logic.

### Data (`core/data/country`)

- `CountryRepositoryImpl` implements `CountryRepository`.
- `CountryRemoteImpl` calls `CountryService` directly — no wrapper layer.
- DTOs (`CountryDto`, etc.) and domain mappers (`Mapper.kt`) live here.
- Exception-to-domain mapping happens inline in `CountryRepositoryImpl.safeCall()` — no intermediate `DataError` type.
- `DataModule` binds all data-layer dependencies via Hilt.

### Network (`core/network`)

- `ServiceFactory` creates Retrofit service instances.
- Retrofit service interfaces use plain `suspend fun` return types (no `Response<T>` wrapper) — Retrofit throws `HttpException` for 4xx/5xx responses natively.
- The network layer has no knowledge of `DataError`, `DomainError`, or any result type — exception translation happens in the data layer.

---

## Result Types

Three typed representations flow through the stack. Each is scoped to its layer. See [`ErrorHandling.md`](./ErrorHandling.md) for the full rules.

```
Infrastructure Exception
        ↓
  safeCall()          ← private, in CountryRepositoryImpl; maps exceptions
        ↓               directly to DomainError / CountryError
  DomainError         ← defined in core:domain:common, crosses the boundary
        ↓
  UiErrorMapper       ← in presentation layer
        ↓
    UiError           ← in presentation layer
        ↓
    UiState           ← consumed by Compose screens
```

### `Result` (`core:domain:common`)

```kotlin
sealed interface Result<out T, out E> {
    data class Success<T>(val data: T) : Result<T, Nothing>
    data class Failure<E>(val error: E) : Result<Nothing, E>
}
```

All operations that may fail return a `Result`. Exceptions must not cross layer boundaries.

### `DomainError` (`core:domain:common`)

```kotlin
interface DomainError {
    object Offline      : DomainError
    object Timeout      : DomainError
    object Unauthorized : DomainError
    object Unexpected   : DomainError
}
```

`DomainError` is a plain interface (not sealed) so that feature modules in separate Gradle modules can extend it. Feature-scoped errors live in their own domain module as sealed interfaces (e.g. `CountryError` in `core:domain:country`). Rules:
- MUST represent business failures, not HTTP codes or infrastructure concepts.
- MUST NOT reference Android framework types.
- Shared errors (`Offline`, `Timeout`, `Unauthorized`, `Unexpected`) live in the base `DomainError`.
- Exhaustive `when` is enforced on `DataError` (sealed, same module) — not on `DomainError`.

### `DataError` (`core:data:country` — internal)

An `internal sealed interface` that classifies infrastructure failures before they are mapped to `DomainError`. Never appears in any public API or outside the data module.

### `UiError` (feature contract)

Sealed interface of user-visible failure cases (`Offline`, `Timeout`, `SessionExpired`, `NotFound`, `Generic`). Defined inside each feature's contract object (e.g. `HomeContract.UiError`). `UiState.Error` carries a `UiError`, not a `DomainError`.

---

## Mappers

### DTO → Domain (`core:data:country/.../mapper/Mapper.kt`)

Extension functions on each DTO type:

```
CountryDto      ──toDomain()──▶  Country
CurrencyDto     ──toDomain()──▶  Currency
LanguageDto     ──toDomain()──▶  Language
RegionalBlocDto ──toDomain()──▶  RegionalBloc
```

All nullable DTO fields are unwrapped with `.orEmpty()` / `?: 0` so the domain model is always non-null.

### Exception → DomainError (`core:data:country/.../repository/CountryRepositoryImpl.kt`)

Exception classification and domain mapping happen in one step inside the private `safeCall()` function:

```
SocketTimeoutException  →  DomainError.Timeout
IOException             →  DomainError.Offline
HttpException 401/403   →  DomainError.Unauthorized
HttpException 404       →  CountryError.NotFound
JsonSyntaxException     →  DomainError.Unexpected
JsonParseException      →  DomainError.Unexpected
Exception (catch-all)   →  DomainError.Unexpected
CancellationException   →  rethrown (never swallowed)
```

This is the single mapping site for infrastructure → domain errors. No intermediate `DataError` type is needed since there is only one repository.

### DomainError → UiError (presentation)

```
DomainError  ──UiErrorMapper.map()──▶  FeatureContract.UiError
```

`UiErrorMapper` lives in the presentation layer and returns the feature's own `UiError` type (e.g. `HomeContract.UiError`). It may use `else` as a safe fallback — unknown domain errors display a generic message rather than crashing.

---

## Navigation

### Pattern

A singleton `Navigator` exposes a `SharedFlow<NavigationEvent>`. ViewModels emit events; `HandleNavigation` in the `app` module collects them and drives the `NavController`.

```
ViewModel  ──navigate(event)──▶  Navigator  ──flow──▶  HandleNavigation  ──▶  NavController
```

### `NavigationEvent` (`core:navigation`)

```kotlin
sealed interface NavigationEvent {
    data object Up
    data object ToHome
    data class ToDetails(val alphaCode: String)
}
```

### `DetailsRoute`

```kotlin
@Serializable
data class DetailsRoute(val alphaCode: String)
```

The Details screen is always opened by alpha code (ISO 3166-1 alpha-3). Both entry points — tapping a country card and tapping a border chip — pass an alpha code, so no runtime disambiguation is needed.

### Flow

1. `HomeViewModel.goToCountryDetails(country)` → `ToDetails(country.alphaCode)`
2. `DetailsViewModel.goToBorderCountry(alphaCode)` → `ToDetails(alphaCode)`
3. `HandleNavigation` → `navController.navigateToDetails(event.alphaCode)`
4. `DetailsViewModel` reads `DetailsRoute.alphaCode` from `SavedStateHandle` → calls `useCase.byAlphaCode(code)`

---

## Dependency Injection

Hilt is used throughout. All modules are installed in `SingletonComponent`.

| Hilt Module | Location | Provides |
|---|---|---|
| `NetworkModule` | `core:network` | `Gson`, `OkHttpClient`, `ServiceFactory` |
| `DataModule` | `core:data:country` | `CountryService`, `CountryRemote`, `CountryRepository` |
| `CountryUseCaseModule` | `core:domain:country` | Use case bindings (`@Binds`) |
| `NavigationModule` | `core:navigation` | `Navigator` singleton |

Use case bindings use `@Binds` (not `@Provides`) since both interface and implementation are in the same module and the impl has an `@Inject` constructor.

---

## Use Cases (`core:domain:country`)

| Use Case | Method(s) | Repository call |
|---|---|---|
| `GetAllCountriesUseCase` | `invoke()` | `countryRepository.getAllCountries()` |
| `GetCountryDetailsUseCase` | `byAlphaCode(code)` | `countryRepository.getCountryByAlphaCode(code)` |

Use cases depend only on domain-owned repository interfaces and must not reference `DataError` or any data-layer type. The `CountryRepository` interface is never referenced directly in a ViewModel.

---

## Data Source

Country data is fetched from `https://www.apicountries.com/`. The API follows the REST Countries v2 schema.

| Endpoint | Service method |
|---|---|
| `GET /countries` | `getAllCountries()` |
| `GET /name/{name}` | `getCountryByName(name)` |
| `GET /alpha/{code}` | `getCountryByAlphaCode(code)` |
