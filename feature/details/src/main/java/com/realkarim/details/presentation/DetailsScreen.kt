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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        onBorderClick = viewModel::goToBorderCountry,
        modifier = modifier
    )
}

@Composable
private fun DetailsScreen(
    uiState: DetailsViewModel.UiState,
    onBorderClick: (String) -> Unit,
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
                borderNames = uiState.borderNames,
                onBorderClick = onBorderClick,
                modifier = Modifier.padding(innerPaddings)
            )
            is DetailsViewModel.UiState.Error -> ErrorContent(
                message = uiState.message,
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
private fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun Details(
    country: Country,
    borderNames: Map<String, String>,
    onBorderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        FlagHero(country = country)

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
                BordersSection(
                    borders = country.borders,
                    borderNames = borderNames,
                    onBorderClick = onBorderClick,
                )
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
private fun FlagHero(country: Country, modifier: Modifier = Modifier) {
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
    modifier: Modifier = Modifier
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
                        onClick = {},
                        label = { Text(item, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BordersSection(
    borders: List<String>,
    borderNames: Map<String, String>,
    onBorderClick: (String) -> Unit,
    modifier: Modifier = Modifier
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
                text = "Borders",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                borders.forEach { code ->
                    val displayName = borderNames[code] ?: code
                    SuggestionChip(
                        onClick = { onBorderClick(displayName) },
                        label = { Text(displayName, style = MaterialTheme.typography.labelMedium) }
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
            borderNames = mapOf("AAA" to "Country A", "BBB" to "Country B"),
        ),
        onBorderClick = {},
    )
}
