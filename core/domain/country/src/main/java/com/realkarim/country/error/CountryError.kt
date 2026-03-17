package com.realkarim.country.error

import com.realkarim.domain.error.DomainError

sealed interface CountryError : DomainError {
    object NotFound : CountryError
}
