package com.realkarim.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.data.service.CountryService
import com.realkarim.domain.Outcome
import com.realkarim.network.NetworkDataSource
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class CountryRemoteImpl @Inject constructor(
    private val homeNetworkDataSource: NetworkDataSource<CountryService>,
) : CountryRemote {
    override suspend fun getAllCountries(): Outcome<List<CountryDto>, ErrorResponse> {
        return homeNetworkDataSource.performRequest { getAllCountries() }
    }
    override suspend fun getCountryByName(countryName: String): Outcome<List<CountryDto>, ErrorResponse> {
        return homeNetworkDataSource.performRequest { getCountryByName(countryName) }
    }
}