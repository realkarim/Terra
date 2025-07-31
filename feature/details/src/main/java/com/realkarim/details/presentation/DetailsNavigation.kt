package com.realkarim.details.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val countryName: String)

fun NavController.navigateToDetails(countryName: String) =
    navigate(route = DetailsRoute(countryName = countryName))

fun NavGraphBuilder.detailsScreen() {
    composable<DetailsRoute> {
        DetailsScreen()
    }
}