package com.realkarim.home.presentation

import com.realkarim.country.error.CountryError
import com.realkarim.domain.error.DomainError
import javax.inject.Inject

class UiErrorMapper @Inject constructor() {

    fun map(error: DomainError): HomeContract.UiError = when (error) {
        DomainError.Offline -> HomeContract.UiError.Offline
        DomainError.Timeout -> HomeContract.UiError.Timeout
        DomainError.Unauthorized -> HomeContract.UiError.SessionExpired
        is CountryError.NotFound -> HomeContract.UiError.NotFound
        else -> HomeContract.UiError.Generic
    }
}
