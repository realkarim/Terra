package com.realkarim.network

import retrofit2.Retrofit

class ServiceFactory(private val retrofit: Retrofit) {
    fun <T> create(service: Class<T>, baseUrl: String): T {
        return retrofit.create(service)
    }
}