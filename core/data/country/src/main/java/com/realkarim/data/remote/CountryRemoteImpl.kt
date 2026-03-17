package com.realkarim.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.data.service.CountryService
import javax.inject.Inject

class CountryRemoteImpl @Inject constructor(
    private val service: CountryService,
) : CountryRemote {

    override suspend fun getAllCountries(): List<CountryDto> =
        service.getAllCountries()

    override suspend fun getCountryByName(countryName: String): List<CountryDto> =
        service.getCountryByName(countryName)

    override suspend fun getCountryByAlphaCode(code: String): CountryDto =
        service.getCountryByAlphaCode(code)
}
