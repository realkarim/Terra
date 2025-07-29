package com.realkarim.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realkarim.domain.model.Country

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onCountryClick = {},
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeViewModel.UiState,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPaddings ->
        when (uiState) {
            is HomeViewModel.UiState.Loading -> {}
            is HomeViewModel.UiState.Success -> CountriesGrid(
                uiState.countries,
                onCountryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
            )

            is HomeViewModel.UiState.Error -> {}
        }
    }
}

@Composable
private fun CountriesGrid(
    countries: List<Country>,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(countries) { country ->
            CountryCard(country = country, onClick = { onCountryClick(country) })
        }
    }
}

@Composable
fun CountryCard(
    country: Country,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = country.name,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "${country.name} flag",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeViewModel.UiState.Success(
            listOf(
                Country(
                    name = "United States",
                    callingCodes = listOf("1"),
                    capital = "Washington, D.C.",
                    subregion = "Americas",
                    region = "Americas",
                    population = 331002651,
                    area = 9833517.0,
                    timezones = listOf("UTC-05:00"),
                    borders = listOf("CAN", "MEX"),
                    nativeName = "United States of America",
                    flagUrl = "https://flagcdn.com/us.png",
                    currencies = emptyList(),
                    languages = emptyList(),
                    regionalBlocs = emptyList()
                ),
                Country(
                    name = "Canada",
                    callingCodes = listOf("1"),
                    capital = "Ottawa",
                    subregion = "Americas",
                    region = "Americas",
                    population = 37742154,
                    area = 9984670.0,
                    timezones = listOf("UTC-05:00"),
                    borders = listOf("USA"),
                    nativeName = "Canada",
                    flagUrl = "https://flagcdn.com/ca.png",
                    currencies = emptyList(),
                    languages = emptyList(),
                    regionalBlocs = emptyList()
                ),
                Country(
                    name = "Mexico",
                    callingCodes = listOf("52"),
                    capital = "Mexico City",
                    subregion = "Americas",
                    region = "Americas",
                    population = 128932753,
                    area = 1964375.0,
                    timezones = listOf("UTC-06:00"),
                    borders = listOf("USA", "GTM", "BLZ"),
                    nativeName = "México",
                    flagUrl = "https://flagcdn.com/mx.png",
                    currencies = emptyList(),
                    languages = emptyList(),
                    regionalBlocs = emptyList()
                ),
                Country(
                    name = "United Kingdom",
                    callingCodes = listOf("44"),
                    capital = "London",
                    subregion = "Europe",
                    region = "Europe",
                    population = 67886011,
                    area = 243610.0,
                    timezones = listOf("UTC+00:00"),
                    borders = emptyList(),
                    nativeName = "United Kingdom of Great Britain and Northern Ireland",
                    flagUrl = "https://flagcdn.com/gb.png",
                    currencies = emptyList(),
                    languages = emptyList(),
                    regionalBlocs = emptyList()
                )
            )
        ),
        onCountryClick = {}
    )
}