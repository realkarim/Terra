package com.realkarim.details.presentation

import com.realkarim.country.model.Country

object DetailsContract {

    sealed interface UiError {
        data object Offline : UiError
        data object Timeout : UiError
        data object SessionExpired : UiError
        data object NotFound : UiError
        data object Generic : UiError
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val country: Country, val isFavourite: Boolean) : UiState
        data class Error(val error: UiError) : UiState
    }

    sealed interface UiEvent {
        data class FavouriteToggled(val country: Country) : UiEvent
    }

    sealed interface SideEffect
}
