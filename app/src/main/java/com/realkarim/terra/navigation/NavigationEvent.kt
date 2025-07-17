package com.realkarim.terra.navigation

sealed interface NavigationEvent {

    data object Up : NavigationEvent

    data object ToHome : NavigationEvent

    data object ToLogin : NavigationEvent
}