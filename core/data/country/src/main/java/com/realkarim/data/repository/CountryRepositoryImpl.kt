package com.realkarim.data.repository

import com.realkarim.country.repository.CountryRepository
import com.realkarim.country.model.Country
import com.realkarim.data.mapper.toDomain
import com.realkarim.data.model.CountryDto
import com.realkarim.data.remote.CountryRemote
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse

class CountryRepositoryImpl(
    private val countryRemote: CountryRemote,
) : CountryRepository {

    override suspend fun getAllCountries(): Outcome<List<Country>, ErrorResponse> {
        return countryRemote.getAllCountries().map { it.map(CountryDto::toDomain) }
    }

    override suspend fun getCountryByName(countryName: String): Outcome<Country, ErrorResponse> {
        return countryRemote.getCountryByName(countryName).map { it.toDomain() }
    }
}