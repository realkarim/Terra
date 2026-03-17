package com.realkarim.data.remote

import com.realkarim.data.model.CountryDto

interface CountryRemote {
    suspend fun getAllCountries(): List<CountryDto>
    suspend fun getCountryByName(countryName: String): List<CountryDto>
    suspend fun getCountryByAlphaCode(code: String): CountryDto
}
