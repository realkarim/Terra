package com.realkarim.country.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result

interface GetAllCountriesUseCase {
    suspend operator fun invoke(): Result<List<Country>, DomainError>
}
