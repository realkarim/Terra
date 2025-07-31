package com.realkarim.domain.result

sealed class DomainOutcome<out DATA, out ERROR> {
    data class Success<out DATA>(val data: DATA) : DomainOutcome<DATA, Nothing>()
    data class Error<out ERROR>(val error: ERROR) : DomainOutcome<Nothing, ERROR>()
    data object Empty : DomainOutcome<Nothing, Nothing>()

    inline fun <TRANSFORMED> map(transform: (DATA) -> TRANSFORMED): DomainOutcome<TRANSFORMED, ERROR> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error)
            Empty -> Empty
        }
}