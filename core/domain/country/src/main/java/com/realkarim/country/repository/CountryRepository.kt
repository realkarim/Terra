package com.realkarim.country.repository

import com.realkarim.country.model.Country
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse

interface CountryRepository {
    suspend fun getAllCountries(): Outcome<List<Country>, ErrorResponse>
    suspend fun getCountryByName(countryName: String): Outcome<Country, ErrorResponse>
}