package com.realkarim.network

import retrofit2.Retrofit
import javax.inject.Inject

class ServiceFactory @Inject constructor(private val retrofitBuilder: Retrofit.Builder) {
    fun <T> create(service: Class<T>, baseUrl: String): T {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .build()
            .create(service)
    }
}