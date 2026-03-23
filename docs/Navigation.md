# Navigation

This project uses **Jetpack Navigation 3** (`androidx.navigation3`). Navigation is driven by a `NavBackStack` that is created in `MainActivity` and passed down to `AppNavHost`, which maps each typed route to its screen.

---

## Key Concepts

| Concept | Role |
| --- |---|
| `NavKey` | Marker interface — every route type implements it |
| `NavBackStack` | Ordered list of route instances; mutating it drives navigation |
| `NavDisplay` | Composable that renders the top of the back stack |
| `entryProvider` | DSL that maps route types to their Composable screens |

---

## Routes

Each feature module declares its own typed route. Routes are `@Serializable` so the back stack can survive process death.

| Route | Type | Args | Module |
| --- |---| --- |---|
| `OnboardingRoute` | `data object` | — | `feature:onboarding` |
| `ListRoute` | `data object` | — | `feature:list` |
| `DetailRoute` | `data class` | `id: String` | `feature:detail` |

Example:

```kotlin
@Serializable
data class DetailRoute(val id: String) : NavKey
```

Routes live inside their feature modules, not in a shared navigation module — the `app` module is the only place that depends on all of them simultaneously.

---

## Navigation Interfaces

Each screen declares a `*Navigation` interface listing the callbacks it needs. This decouples the screen from any knowledge of the back stack:

```kotlin
interface OnboardingNavigation {
    fun onGetStarted()
}

interface ListNavigation {
    fun onItemClick(id: String)
}

interface DetailNavigation {
    fun onRelatedItemClick(id: String)
}
```

Screens call these callbacks; `AppNavHost` implements them.

---

## Back Stack Lifecycle

`MainActivity` creates the back stack with the initial route as the only entry:

```kotlin
val backStack = rememberNavBackStack(OnboardingRoute)
AppNavHost(backStack = backStack)
```

`AppNavHost` receives the back stack and implements all navigation actions inline:

```kotlin
entry<OnboardingRoute> {
    OnboardingScreen(navigation = object : OnboardingNavigation {
        override fun onGetStarted() {
            backStack.clear()          // remove Onboarding from history
            backStack.add(ListRoute)
        }
    })
}

entry<ListRoute> {
    ListScreen(navigation = object : ListNavigation {
        override fun onItemClick(id: String) {
            backStack.add(DetailRoute(id = id))
        }
    })
}

entry<DetailRoute> {
    DetailScreen(
        id = it.id,
        navigation = object : DetailNavigation {
            override fun onRelatedItemClick(id: String) {
                backStack.add(DetailRoute(id = id))  // push another detail entry
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
  └── rememberNavBackStack(OnboardingRoute)
        └── AppNavHost(backStack)
              └── NavDisplay
                    ├── OnboardingRoute  →  OnboardingScreen
                    │     └── onGetStarted()  →  backStack.clear() + add(ListRoute)
                    ├── ListRoute  →  ListScreen
                    │     └── onItemClick(id)  →  backStack.add(DetailRoute(id))
                    └── DetailRoute  →  DetailScreen
                          └── onRelatedItemClick(id)  →  backStack.add(DetailRoute(id))
```

---

## ViewModels and Navigation

ViewModels do **not** interact with the back stack directly. They expose `UiState` and event callbacks that screens forward to their `*Navigation` interface. This keeps ViewModels testable and framework-agnostic.

---

## File Reference

| File | Description |
| --- |---|
| `app/.../MainActivity.kt` | Creates `NavBackStack`, hosts `AppNavHost` |
| `app/.../navigation/AppNavHost.kt` | `NavDisplay` + all `entryProvider` mappings |
| `feature/onboarding/.../OnboardingRoute.kt` | Route key for the Onboarding screen |
| `feature/list/.../ListRoute.kt` | Route key for the List screen |
| `feature/detail/.../DetailRoute.kt` | Route key + `id` arg for the Detail screen |
| `feature/onboarding/.../OnboardingNavigation.kt` | Navigation callbacks for Onboarding |
| `feature/list/.../ListNavigation.kt` | Navigation callbacks for List |
| `feature/detail/.../DetailNavigation.kt` | Navigation callbacks for Detail |
