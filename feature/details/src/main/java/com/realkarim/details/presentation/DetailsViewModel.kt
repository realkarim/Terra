package com.realkarim.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.country.model.Country
import com.realkarim.country.usecase.GetCountryDetailsUseCase
import com.realkarim.domain.result.Result
import com.realkarim.favourites.usecase.ObserveFavouriteStatusUseCase
import com.realkarim.favourites.usecase.ToggleFavouriteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val observeFavouriteStatusUseCase: ObserveFavouriteStatusUseCase,
    private val errorMapper: UiErrorMapper,
    @Assisted private val alphaCode: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(alphaCode: String): DetailsViewModel
    }

    private sealed class CountryLoad {
        object Loading : CountryLoad()
        data class Loaded(val country: Country) : CountryLoad()
        data class Failed(val error: UiError) : CountryLoad()
    }

    private val _countryLoad = MutableStateFlow<CountryLoad>(CountryLoad.Loading)

    val uiState = combine(
        _countryLoad,
        observeFavouriteStatusUseCase(alphaCode),
    ) { load, isFavourite ->
        when (load) {
            CountryLoad.Loading -> UiState.Loading
            is CountryLoad.Loaded -> UiState.Success(load.country, isFavourite)
            is CountryLoad.Failed -> UiState.Error(load.error)
        }
    }
        .onStart { showCountryDetails() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun onFavouriteToggle(country: Country) {
        viewModelScope.launch { toggleFavouriteUseCase(country) }
    }

    private fun showCountryDetails() {
        viewModelScope.launch {
            _countryLoad.value = when (val result = getCountryDetailsUseCase.byAlphaCode(alphaCode)) {
                is Result.Success -> CountryLoad.Loaded(result.data)
                is Result.Failure -> CountryLoad.Failed(errorMapper.map(result.error))
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val country: Country, val isFavourite: Boolean) : UiState()
        data class Error(val error: UiError) : UiState()
    }
}
