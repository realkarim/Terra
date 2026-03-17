package com.realkarim.home.presentation

import com.realkarim.country.error.CountryError
import com.realkarim.domain.error.DomainError
import org.junit.Assert.assertEquals
import org.junit.Test

class UiErrorMapperTest {

    private val mapper = UiErrorMapper()

    @Test
    fun `maps Offline to UiError Offline`() {
        assertEquals(UiError.Offline, mapper.map(DomainError.Offline))
    }

    @Test
    fun `maps Timeout to UiError Timeout`() {
        assertEquals(UiError.Timeout, mapper.map(DomainError.Timeout))
    }

    @Test
    fun `maps Unauthorized to UiError SessionExpired`() {
        assertEquals(UiError.SessionExpired, mapper.map(DomainError.Unauthorized))
    }

    @Test
    fun `maps CountryError NotFound to UiError NotFound`() {
        assertEquals(UiError.NotFound, mapper.map(CountryError.NotFound))
    }

    @Test
    fun `maps Unexpected to UiError Generic`() {
        assertEquals(UiError.Generic, mapper.map(DomainError.Unexpected))
    }
}
