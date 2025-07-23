package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.realkarim.details.presentation.detailsScreen
import com.realkarim.home.presentation.HomeRoute
import com.realkarim.home.presentation.homeScreen
import com.realkarim.welcome.presentation.welcomeScreen

@Composable
fun TerraNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        builder = {
            welcomeScreen()
            homeScreen()
            detailsScreen()
        }
    )
}

