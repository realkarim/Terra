package com.realkarim.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.realkarim.country.model.Country
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCase
import com.realkarim.domain.result.DomainOutcome
import com.realkarim.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Loading
    )
    val uiState = _uiState
        .onStart {
            showCountryDetails()
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private fun showCountryDetails() {
        viewModelScope.launch {
            val countryName = savedStateHandle.toRoute<DetailsRoute>().countryName
            val result = getCountryDetailsUseCase(countryName)
            when (result) {
                is DomainOutcome.Success -> _uiState.update { UiState.Success(result.data) }
                is DomainOutcome.Error -> _uiState.update { UiState.Error("Error Loading Countries") }
                is DomainOutcome.Empty -> _uiState.update { UiState.Error("Empty Response") }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val country: Country) : UiState()
        data class Error(val message: String) : UiState()
    }
}