package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.realkarim.details.presentation.DetailsNavigation
import com.realkarim.details.presentation.DetailsScreen
import com.realkarim.home.presentation.HomeNavigation
import com.realkarim.home.presentation.HomeScreen
import com.realkarim.details.presentation.DetailsRoute
import com.realkarim.home.presentation.HomeRoute
import com.realkarim.welcome.presentation.WelcomeNavigation
import com.realkarim.welcome.presentation.WelcomeRoute
import com.realkarim.welcome.presentation.WelcomeScreen

@Composable
fun TerraNavHost(
    backStack: NavBackStack,
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<WelcomeRoute> {
                WelcomeScreen(
                    navigation = object : WelcomeNavigation {
                        override fun onGetStarted() {
                            backStack.clear()
                            backStack.add(HomeRoute)
                        }
                    }
                )
            }
            entry<HomeRoute> {
                HomeScreen(
                    navigation = object : HomeNavigation {
                        override fun onCountryClick(alphaCode: String) {
                            backStack.add(DetailsRoute(alphaCode = alphaCode))
                        }
                    }
                )
            }
            entry<DetailsRoute> {
                DetailsScreen(
                    alphaCode = it.alphaCode,
                    navigation = object : DetailsNavigation {
                        override fun onBorderCountryClick(alphaCode: String) {
                            backStack.add(DetailsRoute(alphaCode = alphaCode))
                        }
                    }
                )
            }
        },
    )
}
