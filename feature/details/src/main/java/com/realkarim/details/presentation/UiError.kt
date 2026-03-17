package com.realkarim.details.presentation

sealed interface UiError {
    object Offline : UiError
    object Timeout : UiError
    object SessionExpired : UiError
    object NotFound : UiError
    object Generic : UiError
}
