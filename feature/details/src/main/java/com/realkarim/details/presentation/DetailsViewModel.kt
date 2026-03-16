package com.realkarim.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realkarim.country.model.Country
import com.realkarim.country.usecase.GetCountryDetailsUseCase
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    @Assisted private val alphaCode: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(alphaCode: String): DetailsViewModel
    }

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
            when (val result = getCountryDetailsUseCase.byAlphaCode(alphaCode)) {
                is DomainOutcome.Success -> _uiState.update { UiState.Success(result.data) }
                is DomainOutcome.Error -> _uiState.update { UiState.Error(result.error) }
                is DomainOutcome.Empty -> _uiState.update { UiState.Error(DomainError.UnknownError) }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val country: Country) : UiState()
        data class Error(val error: DomainError) : UiState()
    }
}
