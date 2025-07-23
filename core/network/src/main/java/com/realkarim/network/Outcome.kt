package com.realkarim.network

sealed interface Outcome<out D, out E> {
    data class Success<out D>(val data: D) : Outcome<D, Nothing>
    data class Error<out E>(val error: E) : Outcome<Nothing, E>
    data object Empty : Outcome<Nothing, Nothing>
}