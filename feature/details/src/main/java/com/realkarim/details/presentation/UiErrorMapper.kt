package com.realkarim.details.presentation

import com.realkarim.country.error.CountryError
import com.realkarim.domain.error.DomainError
import javax.inject.Inject

class UiErrorMapper @Inject constructor() {

    fun map(error: DomainError): DetailsContract.UiError = when (error) {
        DomainError.Offline -> DetailsContract.UiError.Offline
        DomainError.Timeout -> DetailsContract.UiError.Timeout
        DomainError.Unauthorized -> DetailsContract.UiError.SessionExpired
        is CountryError.NotFound -> DetailsContract.UiError.NotFound
        else -> DetailsContract.UiError.Generic
    }
}
