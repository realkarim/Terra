package com.realkarim.home.presentation

import com.realkarim.country.model.Country
import com.realkarim.country.model.Language
import com.realkarim.country.model.RegionalBloc
import com.realkarim.country.usecase.GetAllCountriesUseCase
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import com.realkarim.favourites.usecase.GetFavouriteCountriesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: GetAllCountriesUseCase = mockk()
    private val getFavouritesUseCase: GetFavouriteCountriesUseCase = mockk()
    private val errorMapper = UiErrorMapper()

    @Before
    fun setUp() {
        every { getFavouritesUseCase() } returns flowOf(emptyList<Country>())
    }

    private fun viewModel() = HomeViewModel(useCase, getFavouritesUseCase, errorMapper)

    // ── initial load ──────────────────────────────────────────────────────────

    @Test
    fun `uiState is Success with countries after successful load`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(2, state.countries.size)
        assertEquals(listOf("Europe"), state.filterOptions.regions)
    }

    @Test
    fun `uiState is Error with mapped UiError on failure`() = runTest {
        coEvery { useCase() } returns Result.Failure(DomainError.Offline)

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        val state = vm.uiState.value as HomeContract.UiState.Error
        assertEquals(HomeContract.UiError.Offline, state.error)
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    fun `search query filters countries by name`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.SearchQueryChanged("ger"))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
        assertEquals("ger", state.searchQuery)
    }

    @Test
    fun `empty search query returns all countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, france))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.SearchQueryChanged("ger"))
        vm.onEvent(HomeContract.UiEvent.SearchQueryChanged(""))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(2, state.countries.size)
    }

    // ── region filter ─────────────────────────────────────────────────────────

    @Test
    fun `selecting a region filters countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(selectedRegions = setOf("Europe"))))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
        assertTrue("Europe" in state.activeFilters.selectedRegions)
    }

    @Test
    fun `resetting filters returns all countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(selectedRegions = setOf("Europe"))))
        vm.onEvent(HomeContract.UiEvent.FiltersReset)
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(2, state.countries.size)
        assertEquals(0, state.activeFilters.activeCount)
    }

    // ── language filter ───────────────────────────────────────────────────────

    @Test
    fun `filtering by language returns matching countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil, japan))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(selectedLanguages = setOf("German"))))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
    }

    // ── population filter ─────────────────────────────────────────────────────

    @Test
    fun `filtering by population bucket returns matching countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil, smallCountry))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(populationBucket = CountryFilter.PopulationBucket.SMALL)))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("SmallCountry", state.countries.first().name)
    }

    // ── area filter ───────────────────────────────────────────────────────────

    @Test
    fun `filtering by area bucket returns matching countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil, tinyCountry))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(areaBucket = CountryFilter.AreaBucket.SMALL)))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("TinyCountry", state.countries.first().name)
    }

    // ── regional bloc filter ──────────────────────────────────────────────────

    @Test
    fun `filtering by regional bloc returns matching countries`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(CountryFilter(selectedRegionalBlocs = setOf("European Union"))))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(1, state.countries.size)
        assertEquals("Germany", state.countries.first().name)
    }

    // ── active count ──────────────────────────────────────────────────────────

    @Test
    fun `activeCount reflects number of active filter criteria`() = runTest {
        coEvery { useCase() } returns Result.Success(listOf(germany, brazil))

        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(HomeContract.UiEvent.FiltersChanged(
            CountryFilter(
                selectedRegions = setOf("Europe"),
                populationBucket = CountryFilter.PopulationBucket.MEDIUM,
            )
        ))
        advanceUntilIdle()

        val state = vm.uiState.value as HomeContract.UiState.Success
        assertEquals(2, state.activeFilters.activeCount)
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private val germany = country(
        name = "Germany",
        region = "Europe",
        alphaCode = "DEU",
        population = 83_000_000L,
        area = 357_114.0,
        languages = listOf(Language(name = "German", nativeName = "Deutsch")),
        regionalBlocs = listOf(RegionalBloc(acronym = "EU", name = "European Union")),
    )
    private val france = country(
        name = "France",
        region = "Europe",
        alphaCode = "FRA",
        population = 67_000_000L,
        area = 551_695.0,
    )
    private val brazil = country(
        name = "Brazil",
        region = "Americas",
        alphaCode = "BRA",
        population = 212_000_000L,
        area = 8_515_767.0,
    )
    private val japan = country(
        name = "Japan",
        region = "Asia",
        alphaCode = "JPN",
        population = 125_000_000L,
        area = 377_930.0,
        languages = listOf(Language(name = "Japanese", nativeName = "日本語")),
    )
    private val smallCountry = country(
        name = "SmallCountry",
        region = "Europe",
        alphaCode = "SML",
        population = 500_000L,
        area = 300.0,
    )
    private val tinyCountry = country(
        name = "TinyCountry",
        region = "Europe",
        alphaCode = "TNY",
        population = 100_000L,
        area = 50.0,
    )

    private fun country(
        name: String,
        region: String,
        alphaCode: String,
        population: Long = 0L,
        area: Double? = null,
        languages: List<Language> = emptyList(),
        regionalBlocs: List<RegionalBloc> = emptyList(),
    ) = Country(
        name = name,
        alphaCode = alphaCode,
        callingCodes = emptyList(),
        capital = "",
        subregion = "",
        region = region,
        population = population,
        area = area,
        timezones = emptyList(),
        borders = emptyList(),
        nativeName = "",
        flagUrl = "",
        currencies = emptyList(),
        languages = languages,
        regionalBlocs = regionalBlocs,
    )
}
