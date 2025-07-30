package com.realkarim.details.domain.repository

import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.network.model.ErrorResponse

interface DetailsRepository {
    suspend fun getCountryByName(countryName: String): Outcome<Country, ErrorResponse>
}