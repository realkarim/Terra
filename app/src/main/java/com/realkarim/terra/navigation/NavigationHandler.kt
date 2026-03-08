package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import com.realkarim.details.presentation.DetailsRoute
import com.realkarim.home.presentation.HomeRoute
import com.realkarim.navigation.NavigationEvent
import com.realkarim.navigation.Navigator

@Composable
fun HandleNavigation(
    navigator: Navigator,
    backStack: NavBackStack,
) {
    LaunchedEffect(navigator) {
        navigator.navigationEventFlow.collect { event ->
            when (event) {
                is NavigationEvent.Up -> backStack.removeLastOrNull()

                is NavigationEvent.ToHome -> {
                    backStack.clear()
                    backStack.add(HomeRoute)
                }

                is NavigationEvent.ToDetails -> {
                    backStack.add(DetailsRoute(alphaCode = event.alphaCode))
                }
            }
        }
    }
}
