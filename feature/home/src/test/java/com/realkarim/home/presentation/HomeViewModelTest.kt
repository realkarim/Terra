package com.realkarim.home.presentation

import com.realkarim.country.model.Country
import com.realkarim.country.usecase.GetAllCountriesUseCase
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: GetAllCountriesUseCase = mockk()
    private val errorMapper = UiErrorMapper()

    // ── initial load ──────────────────────────────────────────────────────────

    @Test
    fun `uiState is Success with countries after successful load`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        val state = vm.uiState.value as HomeViewModel.UiState.Success
        assertEquals(2, state.countries.size)
        assertEquals(listOf("Europe"), state.regions)
    }

    @Test
    fun `uiState is Error with mapped UiError on failure`() = runTest {
        coEvery { useCase() } returns Result.Failure(DomainError.Offline)

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        val state = vm.uiState.value as HomeViewModel.UiState.Error
        assertEquals(UiError.Offline, state.error)
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    fun `search query filters countries by name`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onSearchQueryChange("ger")
        advanceUntilIdle()

        val state = vm.uiState.value as HomeViewModel.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
        assertEquals("ger", state.searchQuery)
    }

    @Test
    fun `empty search query returns all countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onSearchQueryChange("ger")
        vm.onSearchQueryChange("")
        advanceUntilIdle()

        val state = vm.uiState.value as HomeViewModel.UiState.Success
        assertEquals(2, state.countries.size)
    }

    // ── region filter ─────────────────────────────────────────────────────────

    @Test
    fun `selecting a region filters countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onRegionSelected("Europe")
        advanceUntilIdle()

        val state = vm.uiState.value as HomeViewModel.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
        assertEquals("Europe", state.selectedRegion)
    }

    @Test
    fun `selecting the same region again deselects it`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = HomeViewModel(useCase, errorMapper)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onRegionSelected("Europe")
        vm.onRegionSelected("Europe")
        advanceUntilIdle()

        val state = vm.uiState.value as HomeViewModel.UiState.Success
        assertEquals(2, state.countries.size)
        assertEquals(null, state.selectedRegion)
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private val germany = country(name = "Germany", region = "Europe",   alphaCode = "DEU")
    private val france  = country(name = "France",  region = "Europe",   alphaCode = "FRA")
    private val brazil  = country(name = "Brazil",  region = "Americas", alphaCode = "BRA")

    private fun country(name: String, region: String, alphaCode: String) = Country(
        name = name,
        alphaCode = alphaCode,
        callingCodes = emptyList(),
        capital = "",
        subregion = "",
        region = region,
        population = 0L,
        area = null,
        timezones = emptyList(),
        borders = emptyList(),
        nativeName = "",
        flagUrl = "",
        currencies = emptyList(),
        languages = emptyList(),
        regionalBlocs = emptyList(),
    )
}
