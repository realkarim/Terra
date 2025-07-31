package com.realkarim.domain.error

sealed class DomainError {
    data class NetworkError(
        val code: String,
        val message: String,
        val fields: List<String>
    ) : DomainError()

    object UnknownError : DomainError()
}