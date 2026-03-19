package com.realkarim.data.favourites.db.mapper

import com.realkarim.country.model.Country
import com.realkarim.country.model.Currency
import com.realkarim.country.model.Language
import com.realkarim.country.model.RegionalBloc
import com.realkarim.data.favourites.db.FavouriteCountryEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun FavouriteCountryEntity.toDomain(): Country = Country(
    alphaCode = alphaCode,
    name = name,
    capital = capital,
    region = region,
    subregion = subregion,
    nativeName = nativeName,
    flagUrl = flagUrl,
    population = population,
    area = area,
    callingCodes = Json.decodeFromString(callingCodesJson),
    timezones = Json.decodeFromString(timezonesJson),
    borders = Json.decodeFromString(bordersJson),
    currencies = Json.decodeFromString<List<Currency>>(currenciesJson),
    languages = Json.decodeFromString<List<Language>>(languagesJson),
    regionalBlocs = Json.decodeFromString<List<RegionalBloc>>(regionalBlocsJson),
)

fun Country.toEntity(): FavouriteCountryEntity = FavouriteCountryEntity(
    alphaCode = alphaCode,
    name = name,
    capital = capital,
    region = region,
    subregion = subregion,
    nativeName = nativeName,
    flagUrl = flagUrl,
    population = population,
    area = area,
    callingCodesJson = Json.encodeToString(callingCodes),
    timezonesJson = Json.encodeToString(timezones),
    bordersJson = Json.encodeToString(borders),
    currenciesJson = Json.encodeToString(currencies),
    languagesJson = Json.encodeToString(languages),
    regionalBlocsJson = Json.encodeToString(regionalBlocs),
)
