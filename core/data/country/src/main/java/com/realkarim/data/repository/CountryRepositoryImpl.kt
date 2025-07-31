package com.realkarim.data.repository

import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.data.mapper.toDomain
import com.realkarim.data.remote.CountryRemote
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.mapper.toDomainError
import com.realkarim.domain.result.DomainOutcome
import com.realkarim.domain.result.DomainOutcome.Error
import com.realkarim.domain.result.DomainOutcome.Success
import com.realkarim.network.result.NetworkOutcome

class CountryRepositoryImpl(
    private val countryRemote: CountryRemote,
) : CountryRepository {

    override suspend fun getAllCountries(): DomainOutcome<List<Country>, DomainError> {
        val outcome = countryRemote.getAllCountries()
        return when (outcome) {
            is NetworkOutcome.Success -> Success(outcome.data.map { it.toDomain() })
            is NetworkOutcome.Error -> Error(outcome.error.toDomainError())
            is NetworkOutcome.Empty -> DomainOutcome.Empty
        }
    }

    override suspend fun getCountryByName(countryName: String): DomainOutcome<Country, DomainError> {
        val outcome = countryRemote.getCountryByName(countryName)
        return when (outcome) {
            is NetworkOutcome.Success -> Success(outcome.data.first().toDomain())
            is NetworkOutcome.Error -> Error(outcome.error.toDomainError())
            is NetworkOutcome.Empty -> DomainOutcome.Empty
        }
    }
}