package com.realkarim.home.domain.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class GetPopularCountriesUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetPopularCountriesUseCase {

    override suspend fun invoke(): Outcome<List<Country>, ErrorResponse> {
        return countryRepository.getAllCountries()
    }
}