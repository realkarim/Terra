package com.realkarim.home.presentation

sealed interface UiError {
    object Offline : UiError
    object Timeout : UiError
    object SessionExpired : UiError
    object NotFound : UiError
    object Generic : UiError
}
