package com.realkarim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.country.model.Country
import com.realkarim.country.usecase.GetAllCountriesUseCase
import com.realkarim.domain.result.Result
import com.realkarim.favourites.usecase.GetFavouriteCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getFavouriteCountriesUseCase: GetFavouriteCountriesUseCase,
    private val errorMapper: UiErrorMapper,
) : ViewModel() {

    private sealed class LoadResult {
        object Loading : LoadResult()
        data class Loaded(val countries: List<Country>) : LoadResult()
        data class Failed(val error: HomeContract.UiError) : LoadResult()
    }

    private val _loadResult = MutableStateFlow<LoadResult>(LoadResult.Loading)
    private val _searchQuery = MutableStateFlow("")
    private val _showOnlyFavourites = MutableStateFlow(false)
    private val _activeFilters = MutableStateFlow(HomeContract.ActiveFilters())

    val uiState = combine(
        _loadResult,
        _searchQuery,
        _activeFilters,
        _showOnlyFavourites,
        getFavouriteCountriesUseCase(),
    ) { loadResult, query, filters, onlyFavourites, favourites ->
        when (loadResult) {
            LoadResult.Loading -> HomeContract.UiState.Loading
            is LoadResult.Failed -> HomeContract.UiState.Error(loadResult.error)
            is LoadResult.Loaded -> {
                val all = loadResult.countries
                val favouriteAlphaCodes = favourites.map { it.alphaCode }.toSet()
                val filtered = all.filter { country ->
                    matchesSearch(country, query)
                        && matchesFilters(country, filters)
                        && (!onlyFavourites || country.alphaCode in favouriteAlphaCodes)
                }
                HomeContract.UiState.Success(
                    countries = filtered,
                    searchQuery = query,
                    showOnlyFavourites = onlyFavourites,
                    activeFilters = filters,
                    filterOptions = buildAvailableFilters(all),
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeContract.UiState.Loading
    )

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _loadResult.value = when (val result = getAllCountriesUseCase()) {
                is Result.Success -> LoadResult.Loaded(result.data)
                is Result.Failure -> LoadResult.Failed(errorMapper.map(result.error))
            }
        }
    }

    fun onEvent(event: HomeContract.UiEvent) {
        when (event) {
            is HomeContract.UiEvent.SearchQueryChanged -> _searchQuery.value = event.query
            is HomeContract.UiEvent.FiltersChanged -> _activeFilters.value = event.filters
            HomeContract.UiEvent.FavouritesFilterToggled -> _showOnlyFavourites.value = !_showOnlyFavourites.value
            HomeContract.UiEvent.FiltersReset -> _activeFilters.value = HomeContract.ActiveFilters()
        }
    }

    private fun buildAvailableFilters(countries: List<Country>) = HomeContract.AvailableFilters(
        regions = countries.map { it.region }.distinct().sorted(),
        languages = countries.flatMap { it.languages }.map { it.name }.distinct().sorted(),
        regionalBlocs = countries.flatMap { it.regionalBlocs }.map { it.name }.distinct().sorted(),
    )

    private fun matchesSearch(country: Country, query: String): Boolean =
        query.isBlank() || country.name.contains(query, ignoreCase = true)

    private fun matchesFilters(country: Country, filters: HomeContract.ActiveFilters): Boolean {
        if (filters.selectedRegions.isNotEmpty() && country.region !in filters.selectedRegions) return false
        if (filters.selectedLanguages.isNotEmpty() &&
            country.languages.none { it.name in filters.selectedLanguages }) return false
        filters.populationBucket?.let { bucket ->
            if (!matchesPopulation(country.population, bucket)) return false
        }
        filters.areaBucket?.let { bucket ->
            if (!matchesArea(country.area, bucket)) return false
        }
        if (filters.selectedRegionalBlocs.isNotEmpty() &&
            country.regionalBlocs.none { it.name in filters.selectedRegionalBlocs }) return false
        return true
    }

    private fun matchesPopulation(population: Long, bucket: HomeContract.ActiveFilters.PopulationBucket): Boolean =
        when (bucket) {
            HomeContract.ActiveFilters.PopulationBucket.SMALL -> population < 1_000_000L
            HomeContract.ActiveFilters.PopulationBucket.MEDIUM -> population in 1_000_000L..100_000_000L
            HomeContract.ActiveFilters.PopulationBucket.LARGE -> population > 100_000_000L
        }

    private fun matchesArea(area: Double?, bucket: HomeContract.ActiveFilters.AreaBucket): Boolean {
        if (area == null) return false
        return when (bucket) {
            HomeContract.ActiveFilters.AreaBucket.SMALL -> area < 10_000.0
            HomeContract.ActiveFilters.AreaBucket.MEDIUM -> area in 10_000.0..500_000.0
            HomeContract.ActiveFilters.AreaBucket.LARGE -> area > 500_000.0
        }
    }
}
