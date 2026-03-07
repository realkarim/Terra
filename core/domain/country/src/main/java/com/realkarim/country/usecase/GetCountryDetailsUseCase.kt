package com.realkarim.country.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome

interface GetCountryDetailsUseCase {
    suspend fun byAlphaCode(code: String): DomainOutcome<Country, DomainError>
}
