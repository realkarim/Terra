package com.realkarim.home.data.remote

import com.realkarim.domain.Outcome
import com.realkarim.home.data.model.CountryDto
import com.realkarim.network.model.ErrorResponse

interface HomeRemote {
    suspend fun getAllCountries(): Outcome<List<CountryDto>, ErrorResponse>
}