package com.realkarim.data.favourites.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_countries")
data class FavouriteCountryEntity(
    @PrimaryKey val alphaCode: String,
    val name: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val nativeName: String,
    val flagUrl: String,
    val population: Long,
    val area: Double?,
    val callingCodesJson: String,
    val timezonesJson: String,
    val bordersJson: String,
    val currenciesJson: String,
    val languagesJson: String,
    val regionalBlocsJson: String,
)
