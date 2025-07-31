package com.realkarim.home.domain.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome
import javax.inject.Inject

class GetPopularCountriesUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetPopularCountriesUseCase {

    override suspend fun invoke(): DomainOutcome<List<Country>, DomainError> {
        return countryRepository.getAllCountries()
    }
}