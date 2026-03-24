package com.realkarim.home.presentation

data class CountryFilter(
    val selectedRegions: Set<String> = emptySet(),
    val selectedLanguages: Set<String> = emptySet(),
    val populationBucket: PopulationBucket? = null,
    val areaBucket: AreaBucket? = null,
    val selectedRegionalBlocs: Set<String> = emptySet(),
) {
    val activeCount: Int
        get() = selectedRegions.size +
                selectedLanguages.size +
                (if (populationBucket != null) 1 else 0) +
                (if (areaBucket != null) 1 else 0) +
                selectedRegionalBlocs.size

    enum class PopulationBucket(val label: String) {
        SMALL("< 1M"),
        MEDIUM("1M – 100M"),
        LARGE("> 100M"),
    }

    enum class AreaBucket(val label: String) {
        SMALL("< 10K km²"),
        MEDIUM("10K – 500K km²"),
        LARGE("> 500K km²"),
    }
}
