package com.realkarim.country.repository

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.DomainOutcome

interface CountryRepository {
    suspend fun getAllCountries(): DomainOutcome<List<Country>, DomainError>
    suspend fun getCountryByName(countryName: String): DomainOutcome<Country, DomainError>
}