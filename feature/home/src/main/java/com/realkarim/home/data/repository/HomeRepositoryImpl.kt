package com.realkarim.home.data.repository

import com.realkarim.domain.Outcome
import com.realkarim.home.data.mapper.toDomain
import com.realkarim.home.data.model.CountryDto
import com.realkarim.home.data.remote.HomeRemote
import com.realkarim.home.domain.model.Country
import com.realkarim.home.domain.repository.HomeRepository
import com.realkarim.network.model.ErrorResponse

class HomeRepositoryImpl(
    private val homeRemote: HomeRemote,
) : HomeRepository {
    override suspend fun getAllCountries(): Outcome<List<Country>, ErrorResponse> {
        return homeRemote.getAllCountries().map { it.map(CountryDto::toDomain) }
    }
}