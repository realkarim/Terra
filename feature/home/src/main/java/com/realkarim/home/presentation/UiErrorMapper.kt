package com.realkarim.home.presentation

import com.realkarim.country.error.CountryError
import com.realkarim.domain.error.DomainError
import javax.inject.Inject

class UiErrorMapper @Inject constructor() {

    fun map(error: DomainError): UiError = when (error) {
        DomainError.Offline -> UiError.Offline
        DomainError.Timeout -> UiError.Timeout
        DomainError.Unauthorized -> UiError.SessionExpired
        is CountryError.NotFound -> UiError.NotFound
        else -> UiError.Generic
    }
}
