package com.realkarim.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {

    private val _sideEffect = Channel<WelcomeContract.SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: WelcomeContract.UiEvent) {
        when (event) {
            WelcomeContract.UiEvent.GetStartedClicked -> viewModelScope.launch {
                _sideEffect.send(WelcomeContract.SideEffect.NavigateToHome)
            }
        }
    }
}
