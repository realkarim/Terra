package com.realkarim.data.service

import com.realkarim.data.model.CountryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryService {
    @GET("/countries")
    suspend fun getAllCountries(): Response<List<CountryDto>>

    @GET("/name/{countryName}")
    suspend fun getCountryByName(
        @Path("countryName") countryName: String
    ): Response<CountryDto>
}