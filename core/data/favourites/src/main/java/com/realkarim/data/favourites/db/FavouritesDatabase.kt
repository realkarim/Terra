package com.realkarim.data.favourites.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FavouriteCountryEntity::class], version = 1, exportSchema = false)
@TypeConverters(FavouritesTypeConverters::class)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun favouritesDao(): FavouritesDao
}
