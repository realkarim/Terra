package com.realkarim.home.data.remote

import com.realkarim.data.model.CountryDto
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse

interface HomeRemote {
    suspend fun getAllCountries(): Outcome<List<CountryDto>, ErrorResponse>
}