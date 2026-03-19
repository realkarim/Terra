package com.realkarim.data.favourites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    @Query("SELECT * FROM favourite_countries")
    fun observeAll(): Flow<List<FavouriteCountryEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_countries WHERE alphaCode = :alphaCode)")
    fun observeIsFavourite(alphaCode: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_countries WHERE alphaCode = :alphaCode)")
    suspend fun isFavourite(alphaCode: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavouriteCountryEntity)

    @Query("DELETE FROM favourite_countries WHERE alphaCode = :alphaCode")
    suspend fun delete(alphaCode: String)
}
