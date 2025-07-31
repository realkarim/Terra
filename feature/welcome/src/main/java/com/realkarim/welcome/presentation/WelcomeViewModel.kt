package com.realkarim.welcome.presentation

import androidx.lifecycle.ViewModel
import com.realkarim.navigation.NavigationEvent
import com.realkarim.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {
    fun goToHome() {
        navigator.navigate(NavigationEvent.ToHome)
    }
}