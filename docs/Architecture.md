# Architecture

## Overview

This project follows **Clean Architecture** with **MVVM**, enforced through a strict multi-module Gradle setup. Each layer has a defined direction of dependency; inner layers never know about outer ones.

```
Presentation  ──▶  Domain  ◀──  Data  ──▶  Network
(UI / VM)         (Models)      (Repo)
```

---

## Module Graph

```
app/
├── feature:[a]           ──▶  core:domain:[feature]
├── feature:[b]           ──▶  core:domain:[feature]
├── feature:[standalone]  ──▶  (no domain deps)
├── core:data:[feature]   ──▶  core:domain:[feature]
│                         ──▶  core:domain:common
│                         ──▶  core:network
├── core:domain:[feature] ──▶  core:domain:common
├── core:domain:common    ──▶  (standalone)
├── core:network          ──▶  (standalone)
└── core:navigation       ──▶  (standalone)
```

**Rules:**
- Feature modules must NOT depend on each other.
- Domain modules must NOT depend on data or network modules.
- `app` owns the Hilt component; it depends on all feature modules and `core:data:*` (to keep `DataModule` on the Hilt graph).

---

## Layers

### Presentation (`feature/*`)

- Jetpack Compose screens and `@HiltViewModel` ViewModels.
- ViewModels expose a single `StateFlow<UiState>` via `stateIn(WhileSubscribed(5_000))`.
- No business logic — ViewModels call use cases and translate `Result` into `UiState`.
- Errors are mapped from `DomainError` → `UiError` via `UiErrorMapper`; `UiState.Error` carries a `UiError`. Both types are defined in the feature's contract. Screens render `UiError` directly — no error formatting in ViewModels.
- Navigation is triggered by emitting `NavigationEvent` to the `Navigator` singleton; ViewModels never touch `NavController`.

#### Feature Contract

Every feature defines a **contract** — a single file that declares the four building blocks of its presentation layer:

| Type         | Role                                                                                                                                                                                           |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `UiError`    | Sealed interface of user-visible failure cases (`Offline`, `Timeout`, `SessionExpired`, `NotFound`, `Generic`). Carried by `UiState.Error`.                                                    |
| `UiState`    | Sealed interface representing every possible screen state (`Loading`, `Success`, `Error`, …). Exposed as `StateFlow`.                                                                          |
| `UiEvent`    | Sealed interface of user-driven inputs the screen can send to the ViewModel (button taps, text changes, etc.).                                                                                 |
| `SideEffect` | Sealed interface of one-time effects the ViewModel sends to the screen (show snackbar, trigger scroll, etc.). Not to be used for navigation — emit a `NavigationEvent` to `Navigator` instead. |

```
// feature/[name]/presentation/[Name]Contract.kt
object [Name]Contract {
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
| --- |---|
| `core:domain:common` | `DomainError`, `Result` |
| `core:domain:[feature]` | Domain models, repository interface, use case interfaces + impls, Hilt use case module |

Use cases are the entry point into the domain from the presentation layer. Trivial single-repository pass-throughs (no added orchestration or business logic) may be skipped — the ViewModel may call the repository directly. Use cases earn their place when they combine repositories, enforce rules, or apply domain logic.

### Data (`core/data/[feature]`)

- `[Feature]RepositoryImpl` implements the domain-owned repository interface.
- Remote data source calls the Retrofit service directly — no wrapper layer.
- DTOs and domain mappers live here.
- Exception-to-domain mapping happens inside a private `safeCall()` in the repository impl — no intermediate `DataError` type is needed when there is only one repository.
- `DataModule` binds all data-layer dependencies via Hilt.

### Network (`core/network`)

- A service factory creates Retrofit service instances.
- Retrofit service interfaces use plain `suspend fun` return types (no `Response<T>` wrapper) — Retrofit throws `HttpException` for 4xx/5xx responses natively.
- The network layer has no knowledge of `DataError`, `DomainError`, or any result type — exception translation happens in the data layer.

---

## Result Types

Three typed representations flow through the stack. Each is scoped to its layer. See [`ErrorHandling.md`](./ErrorHandling.md) for the full rules.

```
Infrastructure Exception
        ↓
  safeCall()          ← private, in the repository impl; maps exceptions
        ↓               directly to DomainError / feature-scoped error
  DomainError         ← defined in core:domain:common, crosses the boundary
        ↓
  UiErrorMapper       ← in presentation layer
        ↓
    UiError           ← defined in the feature contract
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

`DomainError` is a plain interface (not sealed) so that feature modules in separate Gradle modules can extend it. Feature-scoped errors live in their own domain module as sealed interfaces (e.g. `ItemError` in `core:domain:[feature]`). Rules:
- MUST represent business failures, not HTTP codes or infrastructure concepts.
- MUST NOT reference Android framework types.
- Shared errors (`Offline`, `Timeout`, `Unauthorized`, `Unexpected`) live in the base `DomainError`.
- Exhaustive `when` is enforced on `DataError` (sealed, same module) — not on `DomainError`.

### `DataError` (`core:data:[feature]` — internal)

An `internal sealed interface` that classifies infrastructure failures before they are mapped to `DomainError`. Never appears in any public API or outside the data module.

### `UiError` (feature contract)

Sealed interface of user-visible failure cases (`Offline`, `Timeout`, `SessionExpired`, `NotFound`, `Generic`). Defined inside each feature's contract object (e.g. `ListContract.UiError`). `UiState.Error` carries a `UiError`, not a `DomainError`.

---

## Mappers

### DTO → Domain (`core:data:[feature]/.../mapper/`)

Extension functions on each DTO type:

```
[Item]Dto     ──toDomain()──▶  [Item]
[Sub]Dto      ──toDomain()──▶  [Sub]
```

All nullable DTO fields are unwrapped with `.orEmpty()` / `?: 0` so the domain model is always non-null.

### Exception → DomainError (`core:data:[feature]/.../repository/[Feature]RepositoryImpl.kt`)

Exception classification and domain mapping happen in one step inside the private `safeCall()` function:

```
SocketTimeoutException  →  DomainError.Timeout
IOException             →  DomainError.Offline
HttpException 401/403   →  DomainError.Unauthorized
HttpException 404       →  FeatureError.NotFound
JsonSyntaxException     →  DomainError.Unexpected
JsonParseException      →  DomainError.Unexpected
Exception (catch-all)   →  DomainError.Unexpected
CancellationException   →  rethrown (never swallowed)
```

This is the single mapping site for infrastructure → domain errors. No intermediate `DataError` type is needed when there is only one repository.

### DomainError → UiError (presentation)

```
DomainError  ──UiErrorMapper.map()──▶  [Name]Contract.UiError
```

`UiErrorMapper` lives in the presentation layer and returns the feature's own `UiError` type. It may use `else` as a safe fallback — unknown domain errors display a generic message rather than crashing.

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
    data object ToList
    data class ToDetail(val id: String)
}
```

### Route with arguments

```kotlin
@Serializable
data class DetailRoute(val id: String) : NavKey
```

Arguments required by a destination are carried directly in the route and read from `SavedStateHandle` in the ViewModel.

### Flow

Each ViewModel emits a `NavigationEvent` to the `Navigator`. `HandleNavigation` in `app` maps the event to the appropriate `NavController` call. Any arguments required by the destination route are carried in the event payload and forwarded to the route constructor.

---

## Dependency Injection

Hilt is used throughout. All modules are installed in `SingletonComponent`.

| Hilt Module | Location | Provides |
| --- |---| --- |
| `NetworkModule` | `core:network` | HTTP client, service factory |
| `DataModule` | `core:data:[feature]` | Retrofit service, remote data source, repository binding |
| `[Feature]UseCaseModule` | `core:domain:[feature]` | Use case bindings (`@Binds`) |
| `NavigationModule` | `core:navigation` | `Navigator` singleton |

Use case bindings use `@Binds` (not `@Provides`) since both interface and implementation are in the same module and the impl has an `@Inject` constructor.

---

## Use Cases (`core:domain:[feature]`)

| Use Case | Method(s) | Repository call |
| --- |---| --- |
| `GetAll[Items]UseCase` | `invoke()` | `repository.getAll()` |
| `Get[Item]DetailsUseCase` | `byId(id)` | `repository.getById(id)` |

Use cases depend only on domain-owned repository interfaces and must not reference `DataError` or any data-layer type. Repository interfaces are never referenced directly in a ViewModel.
