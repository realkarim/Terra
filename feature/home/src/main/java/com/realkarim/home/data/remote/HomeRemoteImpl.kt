package com.realkarim.home.data.remote

import com.realkarim.domain.Outcome
import com.realkarim.home.data.model.CountryDto
import com.realkarim.home.data.network.CountryService
import com.realkarim.network.NetworkDataSource
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class HomeRemoteImpl @Inject constructor(
    private val homeNetworkDataSource: NetworkDataSource<CountryService>,
) : HomeRemote {
    override suspend fun getAllCountries(): Outcome<List<CountryDto>, ErrorResponse> {
        return homeNetworkDataSource.performRequest { getAllCountries() }
    }
}