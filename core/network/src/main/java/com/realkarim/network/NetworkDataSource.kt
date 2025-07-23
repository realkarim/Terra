package com.realkarim.network

import com.google.gson.Gson
import com.realkarim.domain.Outcome
import com.realkarim.network.model.ErrorResponse
import retrofit2.Response

class NetworkDataSource<SERVICE>(
    private val service: SERVICE,
    private val gson: Gson,
) {
    suspend fun <R> performRequest(
        request: suspend SERVICE.() -> Response<R>
    ): Outcome<R, ErrorResponse> {
        return try {
            val response = service.request()
            val errorBodyString = response.errorBody()?.string()

            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null && body != Unit) {
                    Outcome.Success(body)
                } else {
                    Outcome.Empty
                }
            } else {
                val error = if (errorBodyString.isNullOrBlank()) {
                    getDefaultErrorResponse()
                } else {
                    getErrorResponse(gson, errorBodyString)
                }
                Outcome.Error(error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val fallbackError = getDefaultErrorResponse()
            Outcome.Error(fallbackError)
        }
    }
}

