package com.realkarim.domain.mapper

import com.realkarim.domain.error.DomainError
import com.realkarim.network.model.ErrorResponse

fun ErrorResponse?.toDomainError(): DomainError {
    return if (this?.errorCode != null || this?.errorMessage != null || !this?.errorFieldList.isNullOrEmpty()) {
        DomainError.NetworkError(
            code = this.errorCode ?: "UNKNOWN_ERROR",
            message = this.errorMessage ?: "An unknown error occurred",
            fields = this.errorFieldList ?: emptyList()
        )
    } else {
        DomainError.UnknownError
    }
}