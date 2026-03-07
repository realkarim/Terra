# Terra

A modern Android application that displays countries from around the world with detailed information, built with Jetpack Compose and Clean Architecture.

## Features

- Browse a list of all countries with flags and basic info
- View detailed information for each country
- Welcome/onboarding screen on first launch

## Architecture

Terra follows **Clean Architecture** with **MVVM**, organized into clear layers:

```
Presentation  -->  Domain  <--  Data
(UI / VM)         (Models)      (Repo / Remote)
```

- **Presentation**: Jetpack Compose screens, ViewModels, Navigation
- **Domain**: Use cases, repository interfaces, domain models, `DomainOutcome`
- **Data**: Repository implementations, Retrofit remote sources, DTOs, mappers

### Result types

| Type | Layer | States |
|---|---|---|
| `NetworkOutcome` | Network | `Success`, `Error`, `Empty` |
| `DomainOutcome` | Domain | `Success`, `Error`, `Empty` |

### Navigation

A singleton `Navigator` exposes a `SharedFlow<NavigationEvent>`. ViewModels emit events; `NavigationHandler` in the app module collects them and drives `NavController`.

## Project Structure

```
Terra/
├── app/                          # Application entry point, theme, NavHost
├── feature/
│   ├── home/                     # Country list screen + ViewModel + use case
│   ├── details/                  # Country details screen + ViewModel + use case
│   └── welcome/                  # Welcome/onboarding screen
└── core/
    ├── data/country/             # CountryRepositoryImpl, CountryRemote, DTOs, mappers
    ├── domain/
    │   ├── common/               # DomainOutcome, DomainError, Mapper interface
    │   └── country/              # Country domain models, CountryRepository interface
    ├── network/                  # NetworkDataSource, NetworkOutcome, ServiceFactory, ErrorHandler
    └── navigation/               # Navigator, NavigationEvent, DI module
```

## Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose, Material3 |
| Architecture | ViewModel, Kotlin Coroutines & Flow |
| DI | Hilt |
| Networking | Retrofit 3, OkHttp, Gson |
| Image loading | Coil |
| Navigation | Jetpack Navigation Compose |
| Serialization | kotlinx.serialization |

**Min SDK**: 24 | **Compile SDK**: 36 | **Kotlin**: 2.2.20 | **AGP**: 8.13.0

## Data Source

Country data is fetched from the [REST Countries API](https://restcountries.com/).
