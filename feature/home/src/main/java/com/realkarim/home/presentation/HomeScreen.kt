package com.realkarim.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realkarim.country.model.Country

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onCountryClick = viewModel::goToCountryDetails,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeViewModel.UiState,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { HomeTopBar() },
    ) { innerPadding ->
        when (uiState) {
            is HomeViewModel.UiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is HomeViewModel.UiState.Success -> CountriesGrid(
                countries = uiState.countries,
                onCountryClick = onCountryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is HomeViewModel.UiState.Error -> ErrorContent(
                message = uiState.message,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Terra",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Explore the world",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(countries) { country ->
            CountryCard(country = country, onClick = { onCountryClick(country) })
        }
    }
}

@Composable
fun CountryCard(
    country: Country,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "${country.name} flag",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (country.capital.isNotBlank()) {
                    Text(
                        text = country.capital,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
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
