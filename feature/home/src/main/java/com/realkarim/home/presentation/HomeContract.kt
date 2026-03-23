package com.realkarim.home.presentation

import com.realkarim.country.model.Country

object HomeContract {

    sealed interface UiError {
        data object Offline : UiError
        data object Timeout : UiError
        data object SessionExpired : UiError
        data object NotFound : UiError
        data object Generic : UiError
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(
            val countries: List<Country>,
            val regions: List<String>,
            val searchQuery: String,
            val selectedRegion: String?,
            val showOnlyFavourites: Boolean,
        ) : UiState
        data class Error(val error: UiError) : UiState
    }

    sealed interface UiEvent {
        data class SearchQueryChanged(val query: String) : UiEvent
        data class RegionSelected(val region: String?) : UiEvent
        data object FavouritesFilterToggled : UiEvent
    }

    sealed interface SideEffect
}
