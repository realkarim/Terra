# Terra — Navigation

Terra uses **Jetpack Navigation 3** (`androidx.navigation3`). Navigation is driven by a `NavBackStack` that is created in `MainActivity` and passed down to `TerraNavHost`, which maps each typed route to its screen.

---

## Key Concepts

| Concept | Role |
|---|---|
| `NavKey` | Marker interface — every route type implements it |
| `NavBackStack` | Ordered list of route instances; mutating it drives navigation |
| `NavDisplay` | Composable that renders the top of the back stack |
| `entryProvider` | DSL that maps route types to their Composable screens |

---

## Routes

Each feature module declares its own typed route. Routes are `@Serializable` so the back stack can survive process death.

| Route | Type | Args | Module |
|---|---|---|---|
| `WelcomeRoute` | `data object` | — | `feature:welcome` |
| `HomeRoute` | `data object` | — | `feature:home` |
| `DetailsRoute` | `data class` | `alphaCode: String` | `feature:details` |

Example:

```kotlin
@Serializable
data class DetailsRoute(val alphaCode: String) : NavKey
```

Routes live inside their feature modules, not in a shared navigation module — the `app` module is the only place that depends on all of them simultaneously.

---

## Navigation Interfaces

Each screen declares a `*Navigation` interface listing the callbacks it needs. This decouples the screen from any knowledge of the back stack:

```kotlin
interface HomeNavigation {
    fun onCountryClick(alphaCode: String)
}

interface DetailsNavigation {
    fun onBorderCountryClick(alphaCode: String)
}

interface WelcomeNavigation {
    fun onGetStarted()
}
```

Screens call these callbacks; `TerraNavHost` implements them.

---

## Back Stack Lifecycle

`MainActivity` creates the back stack with `WelcomeRoute` as the only initial entry:

```kotlin
val backStack = rememberNavBackStack(WelcomeRoute)
TerraNavHost(backStack = backStack)
```

`TerraNavHost` receives the back stack and implements all navigation actions inline:

```kotlin
entry<WelcomeRoute> {
    WelcomeScreen(navigation = object : WelcomeNavigation {
        override fun onGetStarted() {
            backStack.clear()          // remove Welcome from history
            backStack.add(HomeRoute)
        }
    })
}

entry<HomeRoute> {
    HomeScreen(navigation = object : HomeNavigation {
        override fun onCountryClick(alphaCode: String) {
            backStack.add(DetailsRoute(alphaCode = alphaCode))
        }
    })
}

entry<DetailsRoute> {
    DetailsScreen(
        alphaCode = it.alphaCode,
        navigation = object : DetailsNavigation {
            override fun onBorderCountryClick(alphaCode: String) {
                backStack.add(DetailsRoute(alphaCode = alphaCode))  // push another details entry
            }
        }
    )
}
```

Back navigation is handled automatically by `NavDisplay` via the system back gesture/button.

---

## Navigation Flow

```
MainActivity
  └── rememberNavBackStack(WelcomeRoute)
        └── TerraNavHost(backStack)
              └── NavDisplay
                    ├── WelcomeRoute  →  WelcomeScreen
                    │     └── onGetStarted()  →  backStack.clear() + add(HomeRoute)
                    ├── HomeRoute  →  HomeScreen
                    │     └── onCountryClick(code)  →  backStack.add(DetailsRoute(code))
                    └── DetailsRoute  →  DetailsScreen
                          └── onBorderCountryClick(code)  →  backStack.add(DetailsRoute(code))
```

---

## ViewModels and Navigation

ViewModels do **not** interact with the back stack directly. They expose `UiState` and event callbacks that screens forward to their `*Navigation` interface. This keeps ViewModels testable and framework-agnostic.

---

## File Reference

| File | Description |
|---|---|
| `app/.../MainActivity.kt` | Creates `NavBackStack`, hosts `TerraNavHost` |
| `app/.../navigation/TerraNavHost.kt` | `NavDisplay` + all `entryProvider` mappings |
| `feature/welcome/.../WelcomeRoute.kt` | Route key for the Welcome screen |
| `feature/home/.../HomeRoute.kt` | Route key for the Home screen |
| `feature/details/.../DetailsRoute.kt` | Route key + `alphaCode` arg for Details |
| `feature/welcome/.../WelcomeNavigation.kt` | Navigation callbacks for Welcome |
| `feature/home/.../HomeNavigation.kt` | Navigation callbacks for Home |
| `feature/details/.../DetailsNavigation.kt` | Navigation callbacks for Details |
