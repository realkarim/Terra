package com.realkarim.country.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import javax.inject.Inject

class GetCountryDetailsUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetCountryDetailsUseCase {

    override suspend fun byAlphaCode(code: String): Result<Country, DomainError> {
        return countryRepository.getCountryByAlphaCode(code)
    }
}
