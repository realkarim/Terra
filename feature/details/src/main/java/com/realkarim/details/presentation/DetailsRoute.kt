package com.realkarim.details.presentation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val alphaCode: String) : NavKey
