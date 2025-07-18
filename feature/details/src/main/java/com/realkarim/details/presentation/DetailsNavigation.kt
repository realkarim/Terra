package com.realkarim.details.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DetailsRoute

fun NavController.navigateToDetails() = navigate(route = DetailsRoute)

fun NavGraphBuilder.detailsScreen() {
    composable<DetailsRoute> {
        DetailsScreen()
    }
}