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
- No business logic — ViewModels call use cases and translate `DomainOutcome` into `UiState`.
- Navigation is triggered by emitting `NavigationEvent` to the `Navigator` singleton; ViewModels never touch `NavController`.

### Domain (`core/domain/*`)

Two sub-modules:

| Module | Contents |
|---|---|
| `core:domain:common` | `DomainOutcome`, `DomainError` |
| `core:domain:country` | `Country` model (+ nested models), `CountryRepository` interface, use case interfaces + impls, `CountryUseCaseModule` |

Use cases are the only entry point into the domain from the presentation layer. Each use case wraps one or more repository calls and contains any domain logic (e.g. deciding which repository method to invoke).

### Data (`core/data/country`)

- `CountryRepositoryImpl` implements `CountryRepository`.
- `CountryRemoteImpl` wraps the generic `NetworkDataSource<CountryService>`.
- DTOs (`CountryDto`, etc.) and mappers (`Mapper.kt`, `ErrorMapper.kt`) live here.
- `DataModule` binds all data-layer dependencies via Hilt.

### Network (`core/network`)

- `NetworkDataSource<SERVICE>` — generic Retrofit wrapper that executes any `suspend SERVICE.() -> Response<R>` and returns a `NetworkOutcome`.
- `ServiceFactory` creates Retrofit service instances.
- `ErrorHandler` parses error bodies into `ErrorResponse`.

---

## Result Types

Three sealed types flow through the stack, each scoped to its layer:

```
NetworkOutcome<DATA, ErrorResponse>   →   DomainOutcome<DATA, DomainError>   →   UiState
     (network)                                   (domain)                        (presentation)
```

### `NetworkOutcome` (`core:network`)

```kotlin
sealed class NetworkOutcome<out DATA, out ERROR> {
    data class Success<out DATA>(val data: DATA)
    data class Error<out ERROR>(val error: ERROR)   // carries ErrorResponse
    data object Empty
}
```

### `DomainOutcome` (`core:domain:common`)

```kotlin
sealed class DomainOutcome<out DATA, out ERROR> {
    data class Success<out DATA>(val data: DATA)
    data class Error<out ERROR>(val error: ERROR)   // carries DomainError
    data object Empty
}
```

Includes a `map {}` operator for transforming the success value without unwrapping.

### `DomainError` (`core:domain:common`)

```kotlin
sealed class DomainError {
    data class NetworkError(val code: String, val message: String, val fields: List<String>)
    data object UnknownError
}
```

`UiState.Error` carries a `DomainError` (not a string). Screens convert it to a user-facing message via a local `DomainError.toMessage()` extension — the ViewModel never formats strings.

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

### ErrorResponse → DomainError (`core:data:country/.../mapper/ErrorMapper.kt`)

```
ErrorResponse?  ──toDomainError()──▶  DomainError
```

This mapper lives in `core:data:country` (not in `core:domain:common`) to preserve the dependency rule: domain must not know about network types.

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
| `DataModule` | `core:data:country` | `CountryService`, `NetworkDataSource`, `CountryRemote`, `CountryRepository` |
| `CountryUseCaseModule` | `core:domain:country` | Use case bindings (`@Binds`) |
| `NavigationModule` | `core:navigation` | `Navigator` singleton |

Use case bindings use `@Binds` (not `@Provides`) since both interface and implementation are in the same module and the impl has an `@Inject` constructor.

---

## Use Cases (`core:domain:country`)

| Use Case | Method(s) | Repository call |
|---|---|---|
| `GetAllCountriesUseCase` | `invoke()` | `countryRepository.getAllCountries()` |
| `GetCountryDetailsUseCase` | `byAlphaCode(code)` | `countryRepository.getCountryByAlphaCode(code)` |

Use cases are the only classes the presentation layer depends on from the domain. The `CountryRepository` interface is never referenced directly in a ViewModel.

---

## Data Source

Country data is fetched from `https://www.apicountries.com/`. The API follows the REST Countries v2 schema.

| Endpoint | Service method |
|---|---|
| `GET /countries` | `getAllCountries()` |
| `GET /name/{name}` | `getCountryByName(name)` |
| `GET /alpha/{code}` | `getCountryByAlphaCode(code)` |
