package com.realkarim.home.domain.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome

interface GetPopularCountriesUseCase {
    suspend operator fun invoke(): DomainOutcome<List<Country>, DomainError>
}