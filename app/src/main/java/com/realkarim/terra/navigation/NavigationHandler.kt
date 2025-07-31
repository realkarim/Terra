package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.realkarim.details.presentation.navigateToDetails
import com.realkarim.home.presentation.navigateToHome
import com.realkarim.navigation.NavigationEvent
import com.realkarim.navigation.Navigator

@Composable
fun HandleNavigation(
    navigator: Navigator,
    navController: NavController,
) {
    LaunchedEffect(navigator) {
        navigator.navigationEventFlow.collect { event ->

            when (event) {
                is NavigationEvent.Up -> {
                    navController.navigateUp()
                }

                is NavigationEvent.ToHome -> {
                    navController.navigateToHome()
                }

                is NavigationEvent.ToDetails -> {
                    navController.navigateToDetails(event.countryName)
                }
            }
        }
    }
}