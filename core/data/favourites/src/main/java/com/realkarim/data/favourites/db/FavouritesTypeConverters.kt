package com.realkarim.data.favourites.db

import androidx.room.TypeConverter
import com.realkarim.country.model.Currency
import com.realkarim.country.model.Language
import com.realkarim.country.model.RegionalBloc
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavouritesTypeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromCurrencyList(value: List<Currency>): String = Json.encodeToString(value)

    @TypeConverter
    fun toCurrencyList(value: String): List<Currency> = Json.decodeFromString(value)

    @TypeConverter
    fun fromLanguageList(value: List<Language>): String = Json.encodeToString(value)

    @TypeConverter
    fun toLanguageList(value: String): List<Language> = Json.decodeFromString(value)

    @TypeConverter
    fun fromRegionalBlocList(value: List<RegionalBloc>): String = Json.encodeToString(value)

    @TypeConverter
    fun toRegionalBlocList(value: String): List<RegionalBloc> = Json.decodeFromString(value)
}
