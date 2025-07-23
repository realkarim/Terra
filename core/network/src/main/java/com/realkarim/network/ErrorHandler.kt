package com.realkarim.network

import com.google.gson.Gson
import com.realkarim.network.model.ErrorResponse

fun getDefaultErrorResponse() = ErrorResponse("", "", emptyList())

fun getErrorResponse(gson: Gson, errorBodyString: String): ErrorResponse =
    try {
        gson.fromJson(errorBodyString, ErrorResponse::class.java)
    } catch (_: Exception) {
        getDefaultErrorResponse()
    }
