package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.realkarim.details.presentation.DetailsRoute
import com.realkarim.details.presentation.DetailsScreen
import com.realkarim.home.presentation.HomeRoute
import com.realkarim.home.presentation.HomeScreen
import com.realkarim.welcome.presentation.WelcomeRoute
import com.realkarim.welcome.presentation.WelcomeScreen

@Composable
fun TerraNavHost(
    backStack: NavBackStack,
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<WelcomeRoute> { WelcomeScreen() }
            entry<HomeRoute> { HomeScreen() }
            entry<DetailsRoute> { DetailsScreen(alphaCode = it.alphaCode) }
        },
    )
}
