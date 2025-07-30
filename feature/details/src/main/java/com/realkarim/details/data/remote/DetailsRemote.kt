package com.realkarim.details.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse

interface DetailsRemote {
    suspend fun getCountryByName(countryName: String): Outcome<CountryDto, ErrorResponse>
}