package com.realkarim.home.data.model

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name") val name: String?,
    @SerializedName("callingCodes") val callingCodes: List<String>?,
    @SerializedName("capital") val capital: String?,
    @SerializedName("subregion") val subregion: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("population") val population: Long?,
    @SerializedName("area") val area: Double?,
    @SerializedName("timezones") val timezones: List<String>?,
    @SerializedName("borders") val borders: List<String>?,
    @SerializedName("nativeName") val nativeName: String?,
    @SerializedName("flags") val flags: FlagUrlsDto?,
    @SerializedName("currencies") val currencies: List<CurrencyDto>?,
    @SerializedName("languages") val languages: List<LanguageDto>?,
    @SerializedName("regionalBlocs") val regionalBlocs: List<RegionalBlocDto>?
)
