package com.realkarim.country.repository

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result

interface CountryRepository {
    suspend fun getAllCountries(): Result<List<Country>, DomainError>
    suspend fun getCountryByName(countryName: String): Result<Country, DomainError>
    suspend fun getCountryByAlphaCode(code: String): Result<Country, DomainError>
}
