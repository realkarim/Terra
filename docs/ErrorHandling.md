# Modern Android Error Architecture

This document defines a **production‑grade Android error handling
architecture**.

The design minimizes boilerplate while maintaining **strict typed error
boundaries** and **correct dependency direction**: Data → Domain.

------------------------------------------------------------------------

# Architecture Goals

1.  Errors are **explicit values**, not control flow.
2.  Exceptions are **only handled at infrastructure boundaries**.
3.  Errors are **mapped exactly once per layer boundary**.
4.  Mapping logic is **centralized in dedicated Error Mappers**.
5.  Use cases remain **thin orchestration layers**.
6.  The UI receives **only UI‑relevant errors**.
7.  **Repository interfaces are owned by the domain layer.**
8.  **DataError is internal to the data layer and never exposed.**

------------------------------------------------------------------------

# Architecture Layers

The application follows a standard Clean Architecture structure:

Infrastructure\
↓\
Data Layer\
↓\
Domain Layer\
↓\
Presentation Layer

Each layer has its **own error model**. The domain layer defines the
contracts; the data layer implements them.

------------------------------------------------------------------------

# Dependency Rule

The dependency arrow always points inward — toward the domain:

```
:data module  →  depends on  →  :domain module
:presentation →  depends on  →  :domain module
:domain       →  depends on  →  nothing
```

This means:

-   Repository **interfaces** live in `:domain`
-   Repository **implementations** live in `:data`
-   `DataError` is `internal` to `:data` and never crosses the boundary
-   The domain layer never imports anything from `:data`

------------------------------------------------------------------------

# Error Propagation Flow

Infrastructure Exception\
↓\
DataError (internal to data layer)\
↓\
DataErrorMapper (internal to data layer)\
↓\
DomainError (defined by domain, returned by repository impl)\
↓\
UiErrorMapper\
↓\
UiError\
↓\
UiState

- Each boundary must ensure errors are expressed in the receiving layer's model. 
- The data layer translates exceptions into DomainError before returning. 
- The presentation layer translates DomainError into UiError. 
- The domain layer defines DomainError but performs no translation itself.
- The data layer is responsible for the full translation from exceptions to `DomainError` — the domain layer never sees `DataError`.

------------------------------------------------------------------------

# Core Primitive: Result

All operations that may fail MUST return a typed Result.

``` kotlin
sealed interface Result<out T, out E> {

    data class Success<T>(
        val data: T
    ) : Result<T, Nothing>

    data class Failure<E>(
        val error: E
    ) : Result<Nothing, E>
}
```

Usage example:

    Result<User, DomainError>

Rules:

-   Functions that may fail MUST return `Result`
-   Exceptions MUST NOT cross architecture boundaries
-   Success MUST contain valid data
-   Failure MUST contain a typed error

------------------------------------------------------------------------

# Domain Layer

The domain layer **owns the repository interface** and defines the error
type that interface returns. It has zero knowledge of `DataError` or
any infrastructure type.

------------------------------------------------------------------------

# DomainError

Domain errors represent **business meaning**.

As features grow, a single flat sealed interface becomes unmanageable.
Domain errors should be **organized hierarchically** using a common
sealed interface base with **feature-scoped subtypes**.

``` kotlin
// domain/error/DomainError.kt
sealed interface DomainError {

    /** Errors shared across all features. */
    object Offline : DomainError
    object Timeout : DomainError
    object Unauthorized : DomainError
    object Unexpected : DomainError
}
```

``` kotlin
// domain/error/UserError.kt
sealed interface UserError : DomainError {

    object NotFound : UserError
    object Suspended : UserError
    object ProfileIncomplete : UserError
}
```

``` kotlin
// domain/error/PaymentError.kt
sealed interface PaymentError : DomainError {

    object InsufficientFunds : PaymentError
    object CardDeclined : PaymentError
    object InvalidCurrency : PaymentError
}
```

Rules:

-   MUST NOT reference HTTP codes
-   MUST NOT reference Android framework APIs
-   MUST represent business failures
-   Shared errors (Offline, Unauthorized, Timeout, Unexpected) live in the base `DomainError`
-   Feature-specific errors MUST extend `DomainError` as separate sealed interfaces
-   Repository interfaces specify the narrowest error type they can return
    (e.g. `Result<User, UserError>`, `Result<Payment, PaymentError>`)

## Scaling Strategy

| Layer of growth | Approach |
|---|---|
| 1–10 error cases | A single `DomainError` sealed interface is fine |
| 10–30 error cases | Extract feature-scoped interfaces that extend `DomainError` |
| 30+ error cases | Consider grouping by bounded context (e.g., `:domain:payments` module) |

When a use case combines repositories with different error types, it
returns the common base `DomainError`. The `when` branch in the caller
handles both subtypes through the shared hierarchy.

------------------------------------------------------------------------

# Repository Interface (Domain-Owned)

The repository interface lives in the **domain module** and already
speaks `DomainError`. The data layer implements it.

``` kotlin
// domain/repository/UserRepository.kt
interface UserRepository {
    suspend fun getUser(): Result<User, UserError>
    fun observeUser(): Flow<Result<User, UserError>>
}
```

``` kotlin
// domain/repository/SettingsRepository.kt
interface SettingsRepository {
    suspend fun getSettings(): Result<Settings, DomainError>
}
```

Rules:

-   Repository interfaces MUST live in the domain module
-   Repository interfaces MUST return `Result<T, DomainError>` (or a feature-scoped subtype)
-   Repository interfaces MUST NOT reference any data-layer type
-   Reactive repositories MUST return `Flow<Result<T, E>>` (see Flow Error Handling section)

------------------------------------------------------------------------

# Use Case Pattern

Use cases depend only on domain-owned repository interfaces.
No error mapper injection is needed — the result already carries
`DomainError`.

**Trivial pass-through use cases** (single repo call, no added logic)
may be skipped — the ViewModel can call the repository directly.
Use cases earn their place when they add **real orchestration**:
combining repositories, applying validation, or enforcing business rules.

``` kotlin
// domain/usecase/GetUserWithSettingsUseCase.kt
class GetUserWithSettingsUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Result<UserWithSettings, DomainError> {

        val userResult = userRepository.getUser()
        if (userResult is Result.Failure) {
            return Result.Failure(userResult.error)
        }

        val settingsResult = settingsRepository.getSettings()
        if (settingsResult is Result.Failure) {
            return Result.Failure(settingsResult.error)
        }

        val user = (userResult as Result.Success).data
        val settings = (settingsResult as Result.Success).data

        if (!user.isActive) {
            return Result.Failure(UserError.Suspended)
        }

        return Result.Success(
            UserWithSettings(user, settings)
        )
    }
}
```

Rules:

-   Use cases MUST NOT throw exceptions
-   Use cases MUST NOT inject a `DomainErrorMapper`
-   Use cases MUST NOT be aware of `DataError`
-   Trivial pass-through use cases SHOULD be omitted; the ViewModel may call the repository directly
-   Use cases that combine multiple repositories return the common `DomainError` base type

------------------------------------------------------------------------

# Data Layer

The data layer interacts with:

-   HTTP APIs
-   databases
-   file systems
-   external SDKs

The data layer converts **platform exceptions → DataError → DomainError**
entirely internally. Nothing outside the data module ever sees `DataError`.

------------------------------------------------------------------------

# DataError (Internal)

`DataError` is an **internal implementation detail** of the data layer.
It is marked `internal` and never appears in any public API.

``` kotlin
// data/error/DataError.kt
internal sealed interface DataError {

    object Network : DataError

    object Timeout : DataError

    object Unauthorized : DataError

    object Forbidden : DataError

    object NotFound : DataError

    object Serialization : DataError

    object Disk : DataError

    object Unknown : DataError
}
```

Rules:

-   MUST be `internal` — never public
-   MUST represent infrastructure failures
-   MUST NOT contain Android framework types
-   MUST NOT contain business logic
-   MUST NOT be returned by any public API

------------------------------------------------------------------------

# DataErrorMapper (Internal)

The `DataErrorMapper` lives inside the data module and translates
`DataError` into `DomainError`. It is the single mapping site.

The `when` expression MUST be **exhaustive** (no `else` branch). This
ensures the compiler produces an error when a new `DataError` variant is
added, forcing the developer to handle it explicitly instead of silently
falling through to `Unexpected`.

``` kotlin
// data/mapper/DataErrorMapper.kt
internal class DataErrorMapper {

    fun map(error: DataError): DomainError {

        return when (error) {

            DataError.Network ->
                DomainError.Offline

            DataError.Timeout ->
                DomainError.Timeout

            DataError.Unauthorized ->
                DomainError.Unauthorized

            DataError.Forbidden ->
                DomainError.Unauthorized

            DataError.NotFound ->
                UserError.NotFound

            DataError.Serialization ->
                DomainError.Unexpected

            DataError.Disk ->
                DomainError.Unexpected

            DataError.Unknown ->
                DomainError.Unexpected
        }
    }
}
```

Benefits:

-   Mapping is centralized in one place inside the data module
-   Domain and presentation layers are never coupled to this logic
-   Enforces consistent error translation
-   **Exhaustive `when` guarantees compile-time safety when new `DataError` variants are added**

> **Note on `NotFound` mapping:** The example above maps `DataError.NotFound` to
> `UserError.NotFound` because this mapper is used by `UserRepositoryImpl`.
> If the data layer has multiple repositories, each may need its own mapper
> (or a parameterized mapping) to translate `NotFound` into the correct
> feature-scoped domain error.

------------------------------------------------------------------------

# Repository Implementation

The repository implementation lives in the **data module** and
implements the domain-owned interface. It owns both exception catching
and the full translation pipeline.

``` kotlin
// data/repository/UserRepositoryImpl.kt
class UserRepositoryImpl(
    private val api: UserApi,
    private val errorMapper: DataErrorMapper = DataErrorMapper()
) : UserRepository {

    override suspend fun getUser(): Result<User, UserError> {
        return when (val result = fetchUser()) {
            is Result.Success ->
                Result.Success(result.data.toDomain())
            is Result.Failure ->
                Result.Failure(errorMapper.map(result.error))
        }
    }

    private suspend fun fetchUser(): Result<UserDto, DataError> {

        return try {

            Result.Success(api.getUser())

        } catch (e: CancellationException) {

            throw e

        } catch (e: IOException) {

            Result.Failure(DataError.Network)

        } catch (e: SocketTimeoutException) {

            Result.Failure(DataError.Timeout)

        } catch (e: HttpException) {

            when (e.code()) {
                401  -> Result.Failure(DataError.Unauthorized)
                403  -> Result.Failure(DataError.Forbidden)
                404  -> Result.Failure(DataError.NotFound)
                else -> Result.Failure(DataError.Unknown)
            }

        } catch (e: JsonDataException) {

            Result.Failure(DataError.Serialization)

        } catch (e: JsonEncodingException) {

            Result.Failure(DataError.Serialization)

        } catch (e: SerializationException) {

            Result.Failure(DataError.Serialization)

        } catch (e: Exception) {

            Result.Failure(DataError.Unknown)
        }
    }
}
```

Rules:

-   CancellationException MUST be rethrown
-   `SocketTimeoutException` MUST be caught **before** `IOException` (it is a subclass)
-   Serialization exceptions (`JsonDataException`, `JsonEncodingException`, `SerializationException`) MUST be caught explicitly
-   Exception catching MUST happen in a private method that returns `DataError`
-   The public `override` method MUST return `DomainError` only
-   `DataError` MUST NOT appear in the method signature of `override fun getUser()`

> **Serialization note:** Catch the exceptions appropriate for your JSON
> library. Moshi throws `JsonDataException` and `JsonEncodingException`.
> Kotlinx Serialization throws `SerializationException`. Gson throws
> `JsonSyntaxException` and `JsonParseException`. Always place these
> catches **before** the generic `Exception` catch.

------------------------------------------------------------------------

# Flow Error Handling

Many repositories expose reactive streams via `Flow`. Errors inside flows
require special handling because exceptions thrown during collection
propagate differently than in `suspend` functions.

------------------------------------------------------------------------

## Returning `Flow<Result<T, E>>`

Repository interfaces that expose reactive data MUST return
`Flow<Result<T, E>>` — never a raw `Flow<T>` that throws on failure.

``` kotlin
// domain/repository/UserRepository.kt
interface UserRepository {
    suspend fun getUser(): Result<User, UserError>
    fun observeUser(): Flow<Result<User, UserError>>
}
```

------------------------------------------------------------------------

## Safe Flow Wrapper

Use a helper to catch exceptions at the flow boundary and translate
them into `Result.Failure`:

``` kotlin
// data/util/SafeFlow.kt
internal fun <T, E> safeFlow(
    onError: (Throwable) -> E,
    block: suspend FlowCollector<Result<T, E>>.() -> Unit
): Flow<Result<T, E>> = flow(block).catch { throwable ->
    if (throwable is CancellationException) throw throwable
    emit(Result.Failure(onError(throwable)))
}
```

------------------------------------------------------------------------

## Repository Flow Implementation

``` kotlin
// data/repository/UserRepositoryImpl.kt (flow portion)
override fun observeUser(): Flow<Result<User, UserError>> {

    return safeFlow(onError = { mapThrowable(it) }) {

        api.observeUser()
            .map { dto ->
                Result.Success(dto.toDomain()) as Result<User, UserError>
            }
            .collect { emit(it) }
    }
}

private fun mapThrowable(e: Throwable): UserError {
    val dataError = when (e) {
        is IOException            -> DataError.Network
        is SocketTimeoutException -> DataError.Timeout
        is JsonDataException      -> DataError.Serialization
        is SerializationException -> DataError.Serialization
        else                      -> DataError.Unknown
    }
    return errorMapper.map(dataError) as UserError
}
```

------------------------------------------------------------------------

## Collecting Flows in the ViewModel

The ViewModel collects the flow and maps errors to `UiError` as usual.

``` kotlin
// presentation/viewmodel/UserViewModel.kt (flow collection)
fun observe() {

    viewModelScope.launch {

        getUser.observeUser().collect { result ->

            _state.value = when (result) {

                is Result.Success ->
                    UiState.Content(result.data)

                is Result.Failure ->
                    UiState.Error(
                        errorMapper.map(result.error)
                    )
            }
        }
    }
}
```

Rules:

-   Repository flows MUST return `Flow<Result<T, DomainError>>` — never throw
-   `CancellationException` MUST be rethrown inside `catch`
-   Error mapping inside flows follows the same DataError → DomainError pipeline
-   ViewModels MUST NOT use `Flow.catch` for business errors — that is the data layer's responsibility
-   `onCompletion` may be used for cleanup (hiding progress indicators) but MUST NOT swallow errors

------------------------------------------------------------------------

# Presentation Layer

The presentation layer converts:

DomainError → UiError

This translation is handled by **UiErrorMapper**. Unchanged from before
— the presentation layer already only saw `DomainError`.

------------------------------------------------------------------------

# UiError

UiError represents **user‑visible failures**.

``` kotlin
sealed interface UiError {

    object Offline : UiError

    object Timeout : UiError

    object SessionExpired : UiError

    object NotFound : UiError

    object Generic : UiError
}
```

Rules:

-   MUST represent user-visible failures
-   MUST NOT contain infrastructure concepts

------------------------------------------------------------------------

# UiErrorMapper

``` kotlin
class UiErrorMapper {

    fun map(error: DomainError): UiError {

        return when (error) {

            DomainError.Offline ->
                UiError.Offline

            DomainError.Timeout ->
                UiError.Timeout

            DomainError.Unauthorized ->
                UiError.SessionExpired

            is UserError.NotFound ->
                UiError.NotFound

            else ->
                UiError.Generic
        }
    }
}
```

> **Note:** The `UiErrorMapper` uses `else` intentionally. Unlike
> `DataErrorMapper` (which must be exhaustive to catch new variants at
> compile time), the UI layer benefits from a safe fallback — unknown
> domain errors display a generic message rather than crashing.

------------------------------------------------------------------------

# ViewModel Pattern

ViewModels convert use case results into UiState.

``` kotlin
class UserViewModel(
    private val getUserWithSettings: GetUserWithSettingsUseCase,
    private val errorMapper: UiErrorMapper
) : ViewModel() {

    private val _state =
        MutableStateFlow<UiState>(UiState.Loading)

    val state: StateFlow<UiState> = _state

    fun load() {

        viewModelScope.launch {

            when (val result = getUserWithSettings()) {

                is Result.Success ->
                    _state.value =
                        UiState.Content(result.data)

                is Result.Failure ->
                    _state.value =
                        UiState.Error(
                            errorMapper.map(result.error)
                        )
            }
        }
    }
}
```

------------------------------------------------------------------------

# UiState

``` kotlin
sealed interface UiState {

    object Loading : UiState

    data class Content(
        val data: UserWithSettings
    ) : UiState

    data class Error(
        val error: UiError
    ) : UiState
}
```

Rules:

-   UI renders based on UiState
-   UI must not inspect domain errors

------------------------------------------------------------------------

# File Placement Reference

```
:domain module
├── error/
│   ├── DomainError.kt          ← public, base sealed interface
│   ├── UserError.kt            ← public, extends DomainError
│   └── PaymentError.kt         ← public, extends DomainError
├── model/
│   ├── User.kt
│   └── UserWithSettings.kt
├── repository/
│   ├── UserRepository.kt       ← interface, public, owned by domain
│   └── SettingsRepository.kt
└── usecase/
    └── GetUserWithSettingsUseCase.kt

:data module
├── error/
│   └── DataError.kt            ← internal, never exported
├── mapper/
│   └── DataErrorMapper.kt      ← internal, exhaustive when, never exported
├── remote/
│   └── UserApi.kt
├── dto/
│   └── UserDto.kt
├── util/
│   └── SafeFlow.kt             ← internal, flow error boundary helper
└── repository/
    └── UserRepositoryImpl.kt   ← implements domain interface

:presentation module
├── mapper/
│   └── UiErrorMapper.kt
├── state/
│   └── UiState.kt
│   └── UiError.kt
└── viewmodel/
    └── UserViewModel.kt
```

------------------------------------------------------------------------

# Logging Rules

Errors should be logged **once**, where they are classified.

Layer          Logging Policy
  -------------- -----------------------------
Data           log infrastructure failures (at the DataError mapping site)
Domain         no logging
Presentation   optional analytics

Example:

``` kotlin
logger.error("Network error", throwable)
```

------------------------------------------------------------------------

# Retry Rules

Retry logic belongs to the **data layer**.

Allowed strategies:

-   exponential backoff
-   network recovery retry
-   WorkManager retry for background tasks

The UI MAY trigger retry actions but MUST NOT implement retry policies.

------------------------------------------------------------------------

# Cancellation Rules

Coroutine cancellations must never be swallowed.

``` kotlin
catch (e: CancellationException) {
    throw e
}
```

This applies equally in `suspend` functions and in `Flow.catch` blocks.

------------------------------------------------------------------------

# Mandatory Rules Summary

AI agents and developers MUST follow:

1.  Functions that may fail MUST return `Result`
2.  Exceptions MUST NOT cross architecture boundaries
3.  Repository **interfaces** MUST live in the domain module
4.  Repository interfaces MUST return `Result<T, DomainError>` (or a feature-scoped subtype)
5.  `DataError` MUST be `internal` to the data module
6.  The data layer MUST map exceptions → `DataError` → `DomainError` internally
7.  Domain use cases MUST NOT inject or reference `DomainErrorMapper` or `DataError`
8.  Presentation layer maps `DomainError` → `UiError`
9.  Error mapping MUST be centralized in dedicated mapper classes
10. `CancellationException` MUST be rethrown
11. Retry logic MUST live in the data layer
12. `DataErrorMapper` MUST use exhaustive `when` — no `else` branch
13. Serialization exceptions MUST be caught explicitly at the data boundary
14. Reactive repositories MUST return `Flow<Result<T, E>>` — never a raw throwing `Flow<T>`
15. Feature-scoped domain errors MUST extend `DomainError` as separate sealed interfaces
16. Trivial pass-through use cases SHOULD be omitted

------------------------------------------------------------------------

# Final Error Flow

Infrastructure Exception\
↓\
DataError *(internal — data layer only)*\
↓\
DataErrorMapper *(internal — exhaustive mapping, data layer only)*\
↓\
DomainError / feature-scoped subtype *(crosses the boundary — defined by domain)*\
↓\
UiErrorMapper\
↓\
UiError\
↓\
UiState

Errors propagate as **typed values**, not exceptions.\
`DataError` is an **implementation detail**, not a contract.