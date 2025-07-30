package com.realkarim.details.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.data.service.CountryService
import com.realkarim.domain.Outcome
import com.realkarim.network.NetworkDataSource
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class DetailsRemoteImpl @Inject constructor(
    private val homeNetworkDataSource: NetworkDataSource<CountryService>,
) : DetailsRemote {
    override suspend fun getCountryByName(countryName: String): Outcome<CountryDto, ErrorResponse> {
        return homeNetworkDataSource.performRequest { getCountryByName(countryName) }
    }
}