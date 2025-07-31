package com.realkarim.navigation

sealed interface NavigationEvent {

    data object Up : NavigationEvent

    data object ToHome : NavigationEvent

    data object ToDetails : NavigationEvent
}