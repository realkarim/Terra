package com.realkarim.details.domain.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class GetCountryDetailsUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetCountryDetailsUseCase {

    override suspend fun invoke(countryName: String): Outcome<Country, ErrorResponse> {
        return countryRepository.getCountryByName(countryName)
    }
}