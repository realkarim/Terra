package com.realkarim.data.repository

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.realkarim.country.model.Country
import com.realkarim.country.repository.CountryRepository
import com.realkarim.data.error.DataError
import com.realkarim.data.mapper.DataErrorMapper
import com.realkarim.data.mapper.toDomain
import com.realkarim.data.model.CountryDto
import com.realkarim.data.remote.CountryRemote
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class CountryRepositoryImpl(
    private val countryRemote: CountryRemote,
) : CountryRepository {

    private val errorMapper = DataErrorMapper()

    override suspend fun getAllCountries(): Result<List<Country>, DomainError> {
        return when (val result = safeCall { countryRemote.getAllCountries() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Failure -> Result.Failure(errorMapper.map(result.error))
        }
    }

    override suspend fun getCountryByName(countryName: String): Result<Country, DomainError> {
        return when (val result = safeCall { countryRemote.getCountryByName(countryName) }) {
            is Result.Success -> Result.Success(result.data.first().toDomain())
            is Result.Failure -> Result.Failure(errorMapper.map(result.error))
        }
    }

    override suspend fun getCountryByAlphaCode(code: String): Result<Country, DomainError> {
        return when (val result = safeCall { countryRemote.getCountryByAlphaCode(code) }) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Failure -> Result.Failure(errorMapper.map(result.error))
        }
    }

    private suspend fun <T> safeCall(call: suspend () -> T): Result<T, DataError> {
        return try {
            Result.Success(call())
        } catch (e: CancellationException) {
            throw e
        } catch (e: SocketTimeoutException) {
            Result.Failure(DataError.Timeout)
        } catch (e: IOException) {
            Result.Failure(DataError.Network)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Failure(DataError.Unauthorized)
                403 -> Result.Failure(DataError.Forbidden)
                404 -> Result.Failure(DataError.NotFound)
                else -> Result.Failure(DataError.Unknown)
            }
        } catch (e: JsonSyntaxException) {
            Result.Failure(DataError.Serialization)
        } catch (e: JsonParseException) {
            Result.Failure(DataError.Serialization)
        } catch (e: Exception) {
            Result.Failure(DataError.Unknown)
        }
    }
}
