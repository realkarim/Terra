package com.realkarim.data.repository

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.realkarim.country.error.CountryError
import com.realkarim.data.model.CountryDto
import com.realkarim.data.model.FlagUrlsDto
import com.realkarim.data.remote.CountryRemote
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class CountryRepositoryImplTest {

    private val remote: CountryRemote = mockk()
    private val repo = CountryRepositoryImpl(remote)

    // ── getAllCountries ───────────────────────────────────────────────────────

    @Test
    fun `getAllCountries returns Success with mapped domain models`() = runTest {
        coEvery { remote.getAllCountries() } returns listOf(countryDto)

        val result = repo.getAllCountries()

        assertTrue(result is Result.Success)
        val countries = (result as Result.Success).data
        assertEquals(1, countries.size)
        assertEquals("Germany", countries.first().name)
        assertEquals("DEU", countries.first().alphaCode)
    }

    @Test
    fun `getAllCountries returns Offline on IOException`() = runTest {
        coEvery { remote.getAllCountries() } throws IOException()

        val result = repo.getAllCountries()

        assertEquals(Result.Failure(DomainError.Offline), result)
    }

    @Test
    fun `getAllCountries returns Timeout on SocketTimeoutException`() = runTest {
        coEvery { remote.getAllCountries() } throws SocketTimeoutException()

        val result = repo.getAllCountries()

        assertEquals(Result.Failure(DomainError.Timeout), result)
    }

    @Test
    fun `getAllCountries returns Unauthorized on HTTP 401`() = runTest {
        coEvery { remote.getAllCountries() } throws httpException(401)

        assertEquals(Result.Failure(DomainError.Unauthorized), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries returns Unauthorized on HTTP 403`() = runTest {
        coEvery { remote.getAllCountries() } throws httpException(403)

        assertEquals(Result.Failure(DomainError.Unauthorized), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries returns Unexpected on HTTP 500`() = runTest {
        coEvery { remote.getAllCountries() } throws httpException(500)

        assertEquals(Result.Failure(DomainError.Unexpected), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries returns Unexpected on JsonSyntaxException`() = runTest {
        coEvery { remote.getAllCountries() } throws JsonSyntaxException("bad json")

        assertEquals(Result.Failure(DomainError.Unexpected), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries returns Unexpected on JsonParseException`() = runTest {
        coEvery { remote.getAllCountries() } throws JsonParseException("bad json")

        assertEquals(Result.Failure(DomainError.Unexpected), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries returns Unexpected on generic Exception`() = runTest {
        coEvery { remote.getAllCountries() } throws RuntimeException("boom")

        assertEquals(Result.Failure(DomainError.Unexpected), repo.getAllCountries())
    }

    @Test
    fun `getAllCountries rethrows CancellationException`() = runTest {
        coEvery { remote.getAllCountries() } throws CancellationException("cancelled")

        var rethrown = false
        try {
            repo.getAllCountries()
        } catch (e: CancellationException) {
            rethrown = true
        }
        assertTrue("CancellationException must be rethrown", rethrown)
    }

    // ── getCountryByAlphaCode ─────────────────────────────────────────────────

    @Test
    fun `getCountryByAlphaCode returns Success with mapped domain model`() = runTest {
        coEvery { remote.getCountryByAlphaCode("DEU") } returns countryDto

        val result = repo.getCountryByAlphaCode("DEU")

        assertTrue(result is Result.Success)
        assertEquals("DEU", (result as Result.Success).data.alphaCode)
    }

    @Test
    fun `getCountryByAlphaCode returns CountryError NotFound on HTTP 404`() = runTest {
        coEvery { remote.getCountryByAlphaCode("XXX") } throws httpException(404)

        assertEquals(Result.Failure(CountryError.NotFound), repo.getCountryByAlphaCode("XXX"))
    }

    @Test
    fun `getCountryByAlphaCode returns Offline on IOException`() = runTest {
        coEvery { remote.getCountryByAlphaCode("DEU") } throws IOException()

        assertEquals(Result.Failure(DomainError.Offline), repo.getCountryByAlphaCode("DEU"))
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun httpException(code: Int) =
        HttpException(retrofit2.Response.error<Any>(code, "".toResponseBody()))

    private val countryDto = CountryDto(
        name = "Germany",
        alpha3Code = "DEU",
        callingCodes = listOf("49"),
        capital = "Berlin",
        subregion = "Western Europe",
        region = "Europe",
        population = 83_000_000L,
        area = 357_114.0,
        timezones = listOf("UTC+01:00"),
        borders = listOf("AUT", "BEL"),
        nativeName = "Deutschland",
        flags = FlagUrlsDto(svg = null, png = "https://flagcdn.com/de.png"),
        currencies = emptyList(),
        languages = emptyList(),
        regionalBlocs = emptyList(),
    )
}
