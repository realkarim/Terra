package com.realkarim.details.domain.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome

interface GetCountryDetailsUseCase {
    suspend operator fun invoke(countryName: String): DomainOutcome<Country, DomainError>
}