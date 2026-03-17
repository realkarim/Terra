package com.realkarim.data.mapper

import com.realkarim.country.error.CountryError
import com.realkarim.data.error.DataError
import com.realkarim.domain.error.DomainError

internal class DataErrorMapper {

    fun map(error: DataError): DomainError = when (error) {
        DataError.Network -> DomainError.Offline
        DataError.Timeout -> DomainError.Timeout
        DataError.Unauthorized -> DomainError.Unauthorized
        DataError.Forbidden -> DomainError.Unauthorized
        DataError.NotFound -> CountryError.NotFound
        DataError.Serialization -> DomainError.Unexpected
        DataError.Unknown -> DomainError.Unexpected
    }
}
