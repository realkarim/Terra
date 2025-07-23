package com.realkarim.home.data.network

import com.realkarim.home.data.model.CountryDto
import retrofit2.Response
import retrofit2.http.GET

interface CountryService {
    @GET("/countries")
    suspend fun getAllCountries(): Response<List<CountryDto>>
}