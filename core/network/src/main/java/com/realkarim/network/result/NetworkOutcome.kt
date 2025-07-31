package com.realkarim.network.result

sealed class NetworkOutcome<out DATA, out ERROR> {
    data class Success<out DATA>(val data: DATA) : NetworkOutcome<DATA, Nothing>()
    data class Error<out ERROR>(val error: ERROR) : NetworkOutcome<Nothing, ERROR>()
    data object Empty : NetworkOutcome<Nothing, Nothing>()
}