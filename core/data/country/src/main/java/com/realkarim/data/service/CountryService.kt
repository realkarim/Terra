package com.realkarim.data.service

import com.realkarim.data.model.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryService {
    @GET("/countries")
    suspend fun getAllCountries(): List<CountryDto>

    @GET("/name/{countryName}")
    suspend fun getCountryByName(
        @Path("countryName") countryName: String
    ): List<CountryDto>

    @GET("/alpha/{code}")
    suspend fun getCountryByAlphaCode(
        @Path("code") code: String
    ): CountryDto
}
