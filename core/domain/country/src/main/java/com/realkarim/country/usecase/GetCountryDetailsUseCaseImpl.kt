package com.realkarim.country.usecase

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome
import javax.inject.Inject

class GetCountryDetailsUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetCountryDetailsUseCase {

    override suspend fun invoke(countryName: String): DomainOutcome<Country, DomainError> {
        return if (countryName.length == 3 && countryName.all { it.isLetter() }) {
            countryRepository.getCountryByAlphaCode(countryName)
        } else {
            countryRepository.getCountryByName(countryName)
        }
    }
}
