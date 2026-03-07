package com.realkarim.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.realkarim.country.model.Country
import com.realkarim.details.domain.usecase.GetBorderCountriesUseCase
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCase
import com.realkarim.domain.result.DomainOutcome
import com.realkarim.navigation.NavigationEvent
import com.realkarim.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val getBorderCountriesUseCase: GetBorderCountriesUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState
        .onStart { showCountryDetails() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private fun showCountryDetails() {
        viewModelScope.launch {
            val countryName = savedStateHandle.toRoute<DetailsRoute>().countryName
            when (val result = getCountryDetailsUseCase(countryName)) {
                is DomainOutcome.Success -> {
                    val country = result.data
                    val borderNames = if (country.borders.isNotEmpty()) {
                        getBorderCountriesUseCase(country.borders)
                    } else {
                        emptyMap()
                    }
                    _uiState.update { UiState.Success(country, borderNames) }
                }
                is DomainOutcome.Error -> _uiState.update { UiState.Error("Error Loading Countries") }
                is DomainOutcome.Empty -> _uiState.update { UiState.Error("Empty Response") }
            }
        }
    }

    fun goToBorderCountry(countryName: String) {
        navigator.navigate(NavigationEvent.ToDetails(countryName))
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val country: Country,
            /** Maps alpha-3 code → resolved country name (falls back to code on failure). */
            val borderNames: Map<String, String>,
        ) : UiState()
        data class Error(val message: String) : UiState()
    }
}