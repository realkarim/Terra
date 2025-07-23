package com.realkarim.home.domain.repository

import com.realkarim.domain.Outcome
import com.realkarim.home.domain.model.Country
import com.realkarim.network.model.ErrorResponse

interface HomeRepository {
    suspend fun getAllCountries(): Outcome<List<Country>, ErrorResponse>
}