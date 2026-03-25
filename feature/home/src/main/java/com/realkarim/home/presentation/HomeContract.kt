package com.realkarim.home.presentation

import com.realkarim.country.model.Country

object HomeContract {

    data class ActiveFilters(
        val selectedRegions: Set<String> = emptySet(),
        val selectedLanguages: Set<String> = emptySet(),
        val populationBucket: PopulationBucket? = null,
        val areaBucket: AreaBucket? = null,
        val selectedRegionalBlocs: Set<String> = emptySet(),
    ) {
        val activeCount: Int
            get() = selectedRegions.size +
                    selectedLanguages.size +
                    (if (populationBucket != null) 1 else 0) +
                    (if (areaBucket != null) 1 else 0) +
                    selectedRegionalBlocs.size

        enum class PopulationBucket(val label: String) {
            SMALL("< 1M"),
            MEDIUM("1M – 100M"),
            LARGE("> 100M"),
        }

        enum class AreaBucket(val label: String) {
            SMALL("< 10K km²"),
            MEDIUM("10K – 500K km²"),
            LARGE("> 500K km²"),
        }
    }

    data class AvailableFilters(
        val regions: List<String> = emptyList(),
        val languages: List<String> = emptyList(),
        val regionalBlocs: List<String> = emptyList(),
    )

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
            val activeFilters: ActiveFilters,
            val filterOptions: AvailableFilters,
        ) : UiState
        data class Error(val error: UiError) : UiState
    }

    sealed interface UiEvent {
        data class SearchQueryChanged(val query: String) : UiEvent
        data class FiltersChanged(val filters: ActiveFilters) : UiEvent
        data object FavouritesFilterToggled : UiEvent
        data object FiltersReset : UiEvent
    }

    sealed interface SideEffect
}
