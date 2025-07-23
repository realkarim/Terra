package com.realkarim.domain

sealed class Outcome<out DATA, out ERROR> {
    data class Success<out DATA>(val data: DATA) : Outcome<DATA, Nothing>()
    data class Error<out ERROR>(val error: ERROR) : Outcome<Nothing, ERROR>()
    data object Empty : Outcome<Nothing, Nothing>()

    inline fun <TRANSFORMED> map(transform: (DATA) -> TRANSFORMED): Outcome<TRANSFORMED, ERROR> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error)
            Empty -> Empty
        }
}