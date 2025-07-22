package com.realkarim.home.domain.model

data class Country(
    val name: String,
    val callingCodes: List<String>,
    val capital: String,
    val subregion: String,
    val region: String,
    val population: Long,
    val area: Double?,
    val timezones: List<String>,
    val borders: List<String>,
    val nativeName: String,
    val flagUrl: String,
    val currencies: List<Currency>,
    val languages: List<Language>,
    val regionalBlocs: List<RegionalBloc>
)
