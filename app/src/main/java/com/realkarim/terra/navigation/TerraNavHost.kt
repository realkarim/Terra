package com.realkarim.terra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.realkarim.welcome.presentation.WelcomeRoute
import com.realkarim.welcome.presentation.welcomeScreen

@Composable
fun TerraNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = WelcomeRoute,
        builder = {
            welcomeScreen()
        }
    )
}

