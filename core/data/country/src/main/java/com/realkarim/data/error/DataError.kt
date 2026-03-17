package com.realkarim.data.error

internal sealed interface DataError {
    object Network : DataError
    object Timeout : DataError
    object Unauthorized : DataError
    object Forbidden : DataError
    object NotFound : DataError
    object Serialization : DataError
    object Unknown : DataError
}
