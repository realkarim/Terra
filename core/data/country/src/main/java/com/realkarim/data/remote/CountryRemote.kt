package com.realkarim.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.network.model.ErrorResponse
import com.realkarim.network.result.NetworkOutcome

interface CountryRemote {
    suspend fun getAllCountries(): NetworkOutcome<List<CountryDto>, ErrorResponse>
    suspend fun getCountryByName(countryName: String): NetworkOutcome<List<CountryDto>, ErrorResponse>
}