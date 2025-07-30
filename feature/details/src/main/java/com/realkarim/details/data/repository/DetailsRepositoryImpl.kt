package com.realkarim.details.data.repository

import com.realkarim.data.mapper.toDomain
import com.realkarim.details.data.remote.DetailsRemote
import com.realkarim.details.domain.repository.DetailsRepository
import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.network.model.ErrorResponse

class DetailsRepositoryImpl(
    private val detailsRemote: DetailsRemote,
) : DetailsRepository {
    override suspend fun getCountryByName(countryName: String): Outcome<Country, ErrorResponse> {
        return detailsRemote.getCountryByName(countryName).map { it.toDomain() }
    }
}