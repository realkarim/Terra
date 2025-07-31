package com.realkarim.network

import com.google.gson.Gson
import com.realkarim.network.model.ErrorResponse
import com.realkarim.network.result.NetworkOutcome
import retrofit2.Response

class NetworkDataSource<SERVICE>(
    private val service: SERVICE,
    private val gson: Gson,
) {
    suspend fun <R> performRequest(
        request: suspend SERVICE.() -> Response<R>
    ): NetworkOutcome<R, ErrorResponse> {
        return try {
            val response = service.request()
            val errorBodyString = response.errorBody()?.string()

            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null && body != Unit) {
                    NetworkOutcome.Success(body)
                } else {
                    NetworkOutcome.Empty
                }
            } else {
                val error = if (errorBodyString.isNullOrBlank()) {
                    getDefaultErrorResponse()
                } else {
                    getErrorResponse(gson, errorBodyString)
                }
                NetworkOutcome.Error(error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val fallbackError = getDefaultErrorResponse()
            NetworkOutcome.Error(fallbackError)
        }
    }
}

