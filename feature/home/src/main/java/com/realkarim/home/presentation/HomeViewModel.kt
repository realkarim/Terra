package com.realkarim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.domain.Outcome
import com.realkarim.home.domain.model.Country
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import com.realkarim.home.presentation.HomeViewModel.UiState.Error
import com.realkarim.home.presentation.HomeViewModel.UiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularCountriesUseCase: GetPopularCountriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Loading
    )
    val uiState = _uiState
        .onStart { showPopularCountries() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun showPopularCountries() {
        viewModelScope.launch {
            val result = getPopularCountriesUseCase()
            when (result) {
                is Outcome.Success -> _uiState.update { Success(result.data) }
                is Outcome.Error -> _uiState.update { Error("Error Loading Countries") }
                Outcome.Empty -> _uiState.update { Error("Empty Response") }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val countries: List<Country>) : UiState()
        data class Error(val message: String) : UiState()
    }
}