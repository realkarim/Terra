package com.realkarim.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

@Composable
fun HandleNavigation(
    navigator: Navigator,
    navController: NavController,
) {
    LaunchedEffect(navigator) {
        navigator.navigationEventFlow.collect { event ->

            when (event) {
                NavigationEvent.Up -> {

                }

                NavigationEvent.ToHome -> {

                }

                NavigationEvent.ToLogin -> {

                }
            }
        }
    }
}