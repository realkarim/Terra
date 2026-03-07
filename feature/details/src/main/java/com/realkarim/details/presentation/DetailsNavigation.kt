package com.realkarim.details.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val alphaCode: String)

fun NavController.navigateToDetails(alphaCode: String) =
    navigate(route = DetailsRoute(alphaCode = alphaCode))

fun NavGraphBuilder.detailsScreen() {
    composable<DetailsRoute> {
        DetailsScreen()
    }
}
