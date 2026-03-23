package com.realkarim.welcome.presentation

object WelcomeContract {

    sealed interface UiState {
        data object Idle : UiState
    }

    sealed interface UiEvent {
        data object GetStartedClicked : UiEvent
    }

    sealed interface SideEffect
}
