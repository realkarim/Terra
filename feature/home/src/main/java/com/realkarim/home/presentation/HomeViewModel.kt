package com.realkarim.home.presentation

import androidx.lifecycle.ViewModel
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularCountriesUseCase: GetPopularCountriesUseCase,
) : ViewModel() {
}