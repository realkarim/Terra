package com.realkarim.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.network.model.ErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Loading
    )
    val uiState = _uiState
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun showCountryDetails() {
        viewModelScope.launch {
            val countryId = savedStateHandle.toRoute<Int>()
            val result: Outcome<Country, ErrorResponse>? = null
            when (result!!) {
                is Outcome.Success -> _uiState.update { UiState.Success(result.data) }
                is Outcome.Error -> _uiState.update { UiState.Error("Error Loading Countries") }
                Outcome.Empty -> _uiState.update { UiState.Error("Empty Response") }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val country: Country) : UiState()
        data class Error(val message: String) : UiState()
    }
}