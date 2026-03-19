package com.realkarim.data.favourites.di

import android.content.Context
import androidx.room.Room
import com.realkarim.data.favourites.db.FavouritesDao
import com.realkarim.data.favourites.db.FavouritesDatabase
import com.realkarim.data.favourites.repository.FavouritesRepositoryImpl
import com.realkarim.favourites.repository.FavouritesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FavouritesDataModule {

    @Provides
    @Singleton
    fun provideFavouritesDatabase(@ApplicationContext context: Context): FavouritesDatabase =
        Room.databaseBuilder(context, FavouritesDatabase::class.java, "favourites.db").build()

    @Provides
    @Singleton
    fun provideFavouritesDao(db: FavouritesDatabase): FavouritesDao = db.favouritesDao()

    @Provides
    @Singleton
    fun provideFavouritesRepository(dao: FavouritesDao): FavouritesRepository =
        FavouritesRepositoryImpl(dao)
}
