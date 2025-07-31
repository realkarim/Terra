package com.realkarim.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse

interface CountryRemote {
    suspend fun getAllCountries(): Outcome<List<CountryDto>, ErrorResponse>
    suspend fun getCountryByName(countryName: String): Outcome<List<CountryDto>, ErrorResponse>
}