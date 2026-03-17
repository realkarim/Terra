package com.realkarim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.country.model.Country
import com.realkarim.country.usecase.GetAllCountriesUseCase
import com.realkarim.domain.result.Result
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
    private val errorMapper: UiErrorMapper,
) : ViewModel() {

    private sealed class LoadResult {
        object Loading : LoadResult()
        data class Loaded(val countries: List<Country>) : LoadResult()
        data class Failed(val error: UiError) : LoadResult()
    }

    private val _loadResult = MutableStateFlow<LoadResult>(LoadResult.Loading)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedRegion = MutableStateFlow<String?>(null)

    val uiState = combine(
        _loadResult, _searchQuery, _selectedRegion
    ) { loadResult, query, region ->
        when (loadResult) {
            LoadResult.Loading -> UiState.Loading
            is LoadResult.Failed -> UiState.Error(loadResult.error)
            is LoadResult.Loaded -> {
                val all = loadResult.countries
                val regions = all.map { it.region }.distinct().sorted()
                val filtered = all.filter { country ->
                    (query.isBlank() || country.name.contains(query, ignoreCase = true))
                        && (region == null || country.region == region)
                }
                UiState.Success(
                    countries = filtered,
                    regions = regions,
                    searchQuery = query,
                    selectedRegion = region,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
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

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onRegionSelected(region: String?) {
        _selectedRegion.value = if (_selectedRegion.value == region) null else region
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val countries: List<Country>,
            val regions: List<String>,
            val searchQuery: String,
            val selectedRegion: String?,
        ) : UiState()
        data class Error(val error: UiError) : UiState()
    }
}
