package com.realkarim.details.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        modifier = modifier
    )
}

@Composable
private fun DetailsScreen(
    uiState: DetailsViewModel.UiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPaddings ->
        when (uiState) {
            is DetailsViewModel.UiState.Loading -> {}
            is DetailsViewModel.UiState.Success -> Details(
                country = uiState.country,
                modifier = Modifier.padding(innerPaddings)
            )

            is DetailsViewModel.UiState.Error -> {}
        }
    }
}

@Composable
private fun Details(
    country: Country,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = "${country.name} flag",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = country.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Capital: ${country.capital}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text("Region: ${country.region} - ${country.subregion}")
        Text("Native Name: ${country.nativeName}")
        Text("Calling Codes: ${country.callingCodes.joinToString(", ")}")
        Text("Population: ${country.population}")
        country.area?.let {
            Text("Area: $it km²")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Timezones:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        country.timezones.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Borders:", style = MaterialTheme.typography.titleMedium)
        Text(country.borders.joinToString(", "), style = MaterialTheme.typography.bodyMedium)


        Spacer(modifier = Modifier.height(8.dp))

        Text("Currencies:", style = MaterialTheme.typography.titleMedium)
        country.currencies.forEach {
            Text("- ${it.name} (${it.symbol})")
        }


        Spacer(modifier = Modifier.height(8.dp))
        Text("Languages:", style = MaterialTheme.typography.titleMedium)
        country.languages.forEach {
            Text("- ${it.name} (${it.nativeName})")
        }


        Spacer(modifier = Modifier.height(8.dp))
        Text("Regional Blocs:", style = MaterialTheme.typography.titleMedium)
        country.regionalBlocs.forEach {
            Text("- ${it.acronym}: ${it.name}")
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    DetailsScreen(
        uiState = DetailsViewModel.UiState.Success(
            Country(
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
                borders = listOf("Country A", "Country B"),
                currencies = listOf(),
                languages = listOf(),
                regionalBlocs = listOf()
            )
        )
    )
}
