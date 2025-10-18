package com.realkarim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.country.model.Country
import com.realkarim.domain.result.DomainOutcome
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import com.realkarim.home.presentation.HomeViewModel.UiState.Error
import com.realkarim.home.presentation.HomeViewModel.UiState.Success
import com.realkarim.navigation.NavigationEvent
import com.realkarim.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularCountriesUseCase: GetPopularCountriesUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = showPopularCountries()
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun showPopularCountries(): Flow<UiState> {
        return flow {
            val result = getPopularCountriesUseCase()
            emit(
                when (result) {
                    is DomainOutcome.Success -> Success(result.data)
                    is DomainOutcome.Error -> Error("Error Loading Countries")
                    is DomainOutcome.Empty -> Error("Empty Response")
                }
            )
        }
    }

    fun goToCountryDetails(country: Country) {
        navigator.navigate(NavigationEvent.ToDetails(country.name))
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val countries: List<Country>) : UiState()
        data class Error(val message: String) : UiState()
    }
}