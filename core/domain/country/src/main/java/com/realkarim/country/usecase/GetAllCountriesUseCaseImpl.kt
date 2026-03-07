package com.realkarim.country.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome
import javax.inject.Inject

class GetAllCountriesUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetAllCountriesUseCase {

    override suspend fun invoke(): DomainOutcome<List<Country>, DomainError> {
        return countryRepository.getAllCountries()
    }
}
