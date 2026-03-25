package com.realkarim.data.repository

import com.realkarim.country.error.CountryError
import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.data.common.safeApiCall
import com.realkarim.data.mapper.toDomain
import com.realkarim.data.remote.CountryRemote
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result

class CountryRepositoryImpl(
    private val countryRemote: CountryRemote,
) : CountryRepository {

    private val httpErrorMapper: (Int) -> DomainError = { code ->
        when (code) {
            401, 403 -> DomainError.Unauthorized
            404      -> CountryError.NotFound
            else     -> DomainError.Unexpected
        }
    }

    override suspend fun getAllCountries(): Result<List<Country>, DomainError> {
        return when (val result = safeApiCall(httpErrorMapper) { countryRemote.getAllCountries() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Failure -> Result.Failure(result.error)
        }
    }

    override suspend fun getCountryByName(countryName: String): Result<Country, DomainError> {
        return when (val result = safeApiCall(httpErrorMapper) { countryRemote.getCountryByName(countryName) }) {
            is Result.Success -> Result.Success(result.data.first().toDomain())
            is Result.Failure -> Result.Failure(result.error)
        }
    }

    override suspend fun getCountryByAlphaCode(code: String): Result<Country, DomainError> {
        return when (val result = safeApiCall(httpErrorMapper) { countryRemote.getCountryByAlphaCode(code) }) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Failure -> Result.Failure(result.error)
        }
    }
}
