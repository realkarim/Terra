package com.realkarim.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.verticalScroll
import com.realkarim.designsystem.theme.TerraTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    activeFilters: HomeContract.ActiveFilters,
    filterOptions: HomeContract.AvailableFilters,
    onFiltersChanged: (HomeContract.ActiveFilters) -> Unit,
    onFiltersReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (activeFilters.activeCount > 0) {
                    TextButton(onClick = onFiltersReset) {
                        Text("Reset all")
                    }
                }
            }

            if (filterOptions.regions.isNotEmpty()) {
                FilterSection(title = "Region") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filterOptions.regions.forEach { region ->
                            FilterChip(
                                selected = region in activeFilters.selectedRegions,
                                onClick = {
                                    val updated = activeFilters.selectedRegions.toggle(region)
                                    onFiltersChanged(activeFilters.copy(selectedRegions = updated))
                                },
                                label = { Text(region) },
                            )
                        }
                    }
                }
            }

            FilterSection(title = "Population") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HomeContract.ActiveFilters.PopulationBucket.entries.forEach { bucket ->
                        FilterChip(
                            selected = activeFilters.populationBucket == bucket,
                            onClick = {
                                val updated = if (activeFilters.populationBucket == bucket) null else bucket
                                onFiltersChanged(activeFilters.copy(populationBucket = updated))
                            },
                            label = { Text(bucket.label) },
                        )
                    }
                }
            }

            FilterSection(title = "Area") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HomeContract.ActiveFilters.AreaBucket.entries.forEach { bucket ->
                        FilterChip(
                            selected = activeFilters.areaBucket == bucket,
                            onClick = {
                                val updated = if (activeFilters.areaBucket == bucket) null else bucket
                                onFiltersChanged(activeFilters.copy(areaBucket = updated))
                            },
                            label = { Text(bucket.label) },
                        )
                    }
                }
            }

            if (filterOptions.languages.isNotEmpty()) {
                FilterSection(title = "Language") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filterOptions.languages.forEach { language ->
                            FilterChip(
                                selected = language in activeFilters.selectedLanguages,
                                onClick = {
                                    val updated = activeFilters.selectedLanguages.toggle(language)
                                    onFiltersChanged(activeFilters.copy(selectedLanguages = updated))
                                },
                                label = { Text(language) },
                            )
                        }
                    }
                }
            }

            if (filterOptions.regionalBlocs.isNotEmpty()) {
                FilterSection(title = "Regional Bloc") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filterOptions.regionalBlocs.forEach { bloc ->
                            FilterChip(
                                selected = bloc in activeFilters.selectedRegionalBlocs,
                                onClick = {
                                    val updated = activeFilters.selectedRegionalBlocs.toggle(bloc)
                                    onFiltersChanged(activeFilters.copy(selectedRegionalBlocs = updated))
                                },
                                label = { Text(bloc) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
}

private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (item in this) this - item else this + item

@Preview(showBackground = true, name = "No active filters")
@Composable
private fun FilterBottomSheetPreview() {
    TerraTheme {
        FilterBottomSheet(
            activeFilters = HomeContract.ActiveFilters(),
            filterOptions = HomeContract.AvailableFilters(
                regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania"),
                languages = listOf("Arabic", "English", "French", "German", "Japanese", "Portuguese", "Spanish"),
                regionalBlocs = listOf("African Union", "European Union", "NAFTA"),
            ),
            onFiltersChanged = {},
            onFiltersReset = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "With active filters")
@Composable
private fun FilterBottomSheetActivePreview() {
    TerraTheme {
        FilterBottomSheet(
            activeFilters = HomeContract.ActiveFilters(
                selectedRegions = setOf("Europe"),
                selectedLanguages = setOf("French", "German"),
                populationBucket = HomeContract.ActiveFilters.PopulationBucket.MEDIUM,
            ),
            filterOptions = HomeContract.AvailableFilters(
                regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania"),
                languages = listOf("Arabic", "English", "French", "German", "Japanese", "Portuguese", "Spanish"),
                regionalBlocs = listOf("African Union", "European Union", "NAFTA"),
            ),
            onFiltersChanged = {},
            onFiltersReset = {},
            onDismiss = {},
        )
    }
}
