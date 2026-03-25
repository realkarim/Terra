package com.realkarim.data.common

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(
    httpErrorMapper: (Int) -> DomainError = { DomainError.Unexpected },
    call: suspend () -> T,
): Result<T, DomainError> = try {
    Result.Success(call())
} catch (e: CancellationException) {
    throw e
} catch (e: SocketTimeoutException) {
    Result.Failure(DomainError.Timeout)
} catch (e: IOException) {
    Result.Failure(DomainError.Offline)
} catch (e: HttpException) {
    Result.Failure(httpErrorMapper(e.code()))
} catch (e: JsonSyntaxException) {
    Result.Failure(DomainError.Unexpected)
} catch (e: JsonParseException) {
    Result.Failure(DomainError.Unexpected)
} catch (e: Exception) {
    Result.Failure(DomainError.Unexpected)
}
