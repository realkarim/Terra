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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.realkarim.home.domain.model.Country

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreen(
        countries = emptyList(),
        onCountryClick = {},
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    countries: List<Country>,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPaddings ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = innerPaddings,
            modifier = Modifier
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
        countries = listOf(
            Country(name = "USA", flagUrl = "https://example.com/usa.png"),
            Country(name = "Canada", flagUrl = "https://example.com/canada.png"),
            Country(name = "Mexico", flagUrl = "https://example.com/mexico.png"),
            Country(name = "Brazil", flagUrl = "https://example.com/brazil.png"),
            Country(name = "Argentina", flagUrl = "https://example.com/argentina.png"),
            Country(name = "France", flagUrl = "https://example.com/france.png"),
            Country(name = "Germany", flagUrl = "https://example.com/germany.png"),
            Country(name = "Italy", flagUrl = "https://example.com/italy.png"),
            Country(name = "Spain", flagUrl = "https://example.com/spain.png"),
            Country(name = "Japan", flagUrl = "https://example.com/japan.png"),
        ),
        onCountryClick = {}
    )
}