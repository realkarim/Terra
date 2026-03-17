package com.realkarim.domain.error

sealed interface DomainError {
    object Offline : DomainError
    object Timeout : DomainError
    object Unauthorized : DomainError
    object Unexpected : DomainError
}
