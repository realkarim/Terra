package com.realkarim.home.presentation

import com.realkarim.country.error.CountryError
import com.realkarim.domain.error.DomainError
import org.junit.Assert.assertEquals
import org.junit.Test

class UiErrorMapperTest {

    private val mapper = UiErrorMapper()

    @Test
    fun `maps Offline to UiError Offline`() {
        assertEquals(HomeContract.UiError.Offline, mapper.map(DomainError.Offline))
    }

    @Test
    fun `maps Timeout to UiError Timeout`() {
        assertEquals(HomeContract.UiError.Timeout, mapper.map(DomainError.Timeout))
    }

    @Test
    fun `maps Unauthorized to UiError SessionExpired`() {
        assertEquals(HomeContract.UiError.SessionExpired, mapper.map(DomainError.Unauthorized))
    }

    @Test
    fun `maps CountryError NotFound to UiError NotFound`() {
        assertEquals(HomeContract.UiError.NotFound, mapper.map(CountryError.NotFound))
    }

    @Test
    fun `maps Unexpected to UiError Generic`() {
        assertEquals(HomeContract.UiError.Generic, mapper.map(DomainError.Unexpected))
    }
}
