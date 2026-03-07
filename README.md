# Terra

A modern Android app that lets you explore every country in the world — browse flags, read geographic and demographic details, and jump between neighbouring countries.

## Features

- **Country browser** — scrollable grid of all countries with flag images, name, and capital
- **Search** — filter countries by name in real time
- **Region filter** — narrow the list by continent via chip filters
- **Country details** — flag hero image, location info (capital, region, subregion), demographics (population, area, native name, calling codes), timezones, currencies, languages, and regional blocs
- **Border navigation** — tap any border chip to jump directly to that neighbouring country
- **Welcome screen** — shown on first launch

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

## Project Structure

```
Terra/
├── app/                        # Entry point, theme, NavHost, NavigationHandler
├── feature/
│   ├── home/                   # Country grid, search, region filter
│   ├── details/                # Country detail screen, border navigation
│   └── welcome/                # Onboarding screen
└── core/
    ├── data/country/           # Repository impl, remote source, DTOs, mappers
    ├── domain/
    │   ├── common/             # DomainOutcome, DomainError
    │   └── country/            # Country models, repository interface, use cases
    ├── network/                # NetworkDataSource, NetworkOutcome, ServiceFactory
    └── navigation/             # Navigator, NavigationEvent
```

## Documentation

- [`docs/Architecture.md`](./docs/Architecture.md) — layers, module graph, result types, mappers, navigation flow, DI setup
