package com.realkarim.details.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realkarim.country.model.Country

@Composable
fun DetailsScreen(
    alphaCode: String,
    navigation: DetailsNavigation,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
        key = alphaCode,
        creationCallback = { factory -> factory.create(alphaCode) }
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        onBorderClick = navigation::onBorderCountryClick,
        onFavouriteToggle = viewModel::onFavouriteToggle,
        modifier = modifier
    )
}

@Composable
private fun DetailsScreen(
    uiState: DetailsViewModel.UiState,
    onBorderClick: (String) -> Unit,
    onFavouriteToggle: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPaddings ->
        when (uiState) {
            is DetailsViewModel.UiState.Loading -> LoadingContent(
                modifier = Modifier.padding(innerPaddings).fillMaxSize()
            )
            is DetailsViewModel.UiState.Success -> Details(
                country = uiState.country,
                isFavourite = uiState.isFavourite,
                onBorderClick = onBorderClick,
                onFavouriteToggle = onFavouriteToggle,
                modifier = Modifier.padding(innerPaddings)
            )
            is DetailsViewModel.UiState.Error -> ErrorContent(
                error = uiState.error,
                modifier = Modifier.padding(innerPaddings).fillMaxSize()
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
private fun ErrorContent(error: UiError, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = when (error) {
                UiError.Offline -> "No internet connection"
                UiError.Timeout -> "Request timed out"
                UiError.SessionExpired -> "Session expired"
                UiError.NotFound -> "Country not found"
                UiError.Generic -> "Something went wrong"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun Details(
    country: Country,
    isFavourite: Boolean,
    onBorderClick: (String) -> Unit,
    onFavouriteToggle: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        FlagHero(country = country, isFavourite = isFavourite, onFavouriteToggle = onFavouriteToggle)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(title = "Location") {
                InfoRow(label = "Capital", value = country.capital)
                InfoRow(label = "Region", value = country.region)
                InfoRow(label = "Subregion", value = country.subregion)
            }

            InfoCard(title = "Demographics") {
                InfoRow(label = "Population", value = "%,d".format(country.population))
                country.area?.let { InfoRow(label = "Area", value = "%.0f km²".format(it)) }
                InfoRow(label = "Native Name", value = country.nativeName)
                InfoRow(
                    label = "Calling Codes",
                    value = country.callingCodes.joinToString(", ") { "+$it" }
                )
            }

            if (country.timezones.isNotEmpty()) {
                ChipSection(title = "Timezones", items = country.timezones)
            }

            if (country.borders.isNotEmpty()) {
                ChipSection(title = "Borders", items = country.borders, onItemClick = onBorderClick)
            }

            if (country.currencies.isNotEmpty()) {
                InfoCard(title = "Currencies") {
                    country.currencies.forEach { currency ->
                        InfoRow(label = currency.name, value = "${currency.symbol} (${currency.code})")
                    }
                }
            }

            if (country.languages.isNotEmpty()) {
                InfoCard(title = "Languages") {
                    country.languages.forEach { language ->
                        InfoRow(label = language.name, value = language.nativeName)
                    }
                }
            }

            if (country.regionalBlocs.isNotEmpty()) {
                InfoCard(title = "Regional Blocs") {
                    country.regionalBlocs.forEach { bloc ->
                        InfoRow(label = bloc.acronym, value = bloc.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FlagHero(
    country: Country,
    isFavourite: Boolean,
    onFavouriteToggle: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = "${country.name} flag",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { onFavouriteToggle(country) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                tint = if (isFavourite) MaterialTheme.colorScheme.error else Color.White,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (country.nativeName.isNotBlank() && country.nativeName != country.name) {
                    Text(
                        text = country.nativeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun ChipSection(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    onItemClick: ((String) -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEach { item ->
                    SuggestionChip(
                        onClick = { onItemClick?.invoke(item) },
                        label = { Text(item, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    DetailsScreen(
        uiState = DetailsViewModel.UiState.Success(
            country = Country(
                name = "Testland",
                alphaCode = "TST",
                capital = "Test City",
                region = "Test Region",
                subregion = "Test Subregion",
                nativeName = "Test Native Name",
                callingCodes = listOf("123"),
                population = 1000000,
                area = 12345.67,
                flagUrl = "https://example.com/flag.png",
                timezones = listOf("UTC+0"),
                borders = listOf("AAA", "BBB"),
                currencies = listOf(),
                languages = listOf(),
                regionalBlocs = listOf()
            ),
            isFavourite = false,
        ),
        onBorderClick = {},
        onFavouriteToggle = {},
    )
}
