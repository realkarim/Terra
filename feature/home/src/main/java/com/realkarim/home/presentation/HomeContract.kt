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
            val searchQuery: String,
            val showOnlyFavourites: Boolean,
            val activeFilters: CountryFilter,
            val filterOptions: FilterOptions,
        ) : UiState
        data class Error(val error: UiError) : UiState
    }

    sealed interface UiEvent {
        data class SearchQueryChanged(val query: String) : UiEvent
        data class FiltersChanged(val filters: CountryFilter) : UiEvent
        data object FavouritesFilterToggled : UiEvent
        data object FiltersReset : UiEvent
    }

    sealed interface SideEffect
}
