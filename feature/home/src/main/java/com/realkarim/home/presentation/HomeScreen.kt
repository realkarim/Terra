package com.realkarim.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    navigation: HomeNavigation,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onCountryClick = { country -> navigation.onCountryClick(country.alphaCode) },
        onSearchQueryChange = { viewModel.onEvent(HomeContract.UiEvent.SearchQueryChanged(it)) },
        onFavouritesFilterToggle = { viewModel.onEvent(HomeContract.UiEvent.FavouritesFilterToggled) },
        onFiltersChanged = { viewModel.onEvent(HomeContract.UiEvent.FiltersChanged(it)) },
        onFiltersReset = { viewModel.onEvent(HomeContract.UiEvent.FiltersReset) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeContract.UiState,
    onCountryClick: (Country) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFavouritesFilterToggle: () -> Unit,
    onFiltersChanged: (HomeContract.ActiveFilters) -> Unit,
    onFiltersReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { HomeTopBar() },
    ) { innerPadding ->
        when (uiState) {
            is HomeContract.UiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is HomeContract.UiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SearchField(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
                FilterRow(
                    showOnlyFavourites = uiState.showOnlyFavourites,
                    onFavouritesFilterToggle = onFavouritesFilterToggle,
                    activeFilterCount = uiState.activeFilters.activeCount,
                    onFilterClick = { showFilterSheet = true },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
                if (uiState.countries.isEmpty()) {
                    EmptySearchContent(modifier = Modifier.fillMaxSize())
                } else {
                    CountriesGrid(
                        countries = uiState.countries,
                        onCountryClick = onCountryClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                if (showFilterSheet) {
                    FilterBottomSheet(
                        activeFilters = uiState.activeFilters,
                        filterOptions = uiState.filterOptions,
                        onFiltersChanged = onFiltersChanged,
                        onFiltersReset = onFiltersReset,
                        onDismiss = { showFilterSheet = false },
                    )
                }
            }
            is HomeContract.UiState.Error -> ErrorContent(
                error = uiState.error,
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
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search countries...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        ),
    )
}

@Composable
private fun FilterRow(
    showOnlyFavourites: Boolean,
    onFavouritesFilterToggle: () -> Unit,
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = showOnlyFavourites,
            onClick = onFavouritesFilterToggle,
            label = { Text("Favourites") },
        )
        BadgedBox(
            badge = {
                if (activeFilterCount > 0) {
                    Badge { Text("$activeFilterCount") }
                }
            }
        ) {
            FilterChip(
                selected = activeFilterCount > 0,
                onClick = onFilterClick,
                label = { Text("Filters") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(error: HomeContract.UiError, modifier: Modifier = Modifier) {
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
                text = when (error) {
                    HomeContract.UiError.Offline -> "No internet connection"
                    HomeContract.UiError.Timeout -> "Request timed out"
                    HomeContract.UiError.SessionExpired -> "Session expired"
                    HomeContract.UiError.NotFound -> "Countries not found"
                    HomeContract.UiError.Generic -> "An unexpected error occurred"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptySearchContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "No countries found",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Try a different name or filter",
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
        uiState = HomeContract.UiState.Success(
            countries = listOf(
                Country(
                    name = "United States",
                    alphaCode = "USA",
                    callingCodes = listOf("1"),
                    capital = "Washington, D.C.",
                    subregion = "Northern America",
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
                    alphaCode = "CAN",
                    callingCodes = listOf("1"),
                    capital = "Ottawa",
                    subregion = "Northern America",
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
            ),
            searchQuery = "",
            showOnlyFavourites = false,
            activeFilters = HomeContract.ActiveFilters(),
            filterOptions = HomeContract.AvailableFilters(regions = listOf("Americas", "Europe")),
        ),
        onCountryClick = {},
        onSearchQueryChange = {},
        onFavouritesFilterToggle = {},
        onFiltersChanged = {},
        onFiltersReset = {},
    )
}
