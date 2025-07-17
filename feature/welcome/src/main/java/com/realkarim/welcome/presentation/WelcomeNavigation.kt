package com.realkarim.welcome.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

fun NavGraphBuilder.welcomeScreen() {
    composable<WelcomeRoute> {
        WelcomeScreen {}
    }
}
