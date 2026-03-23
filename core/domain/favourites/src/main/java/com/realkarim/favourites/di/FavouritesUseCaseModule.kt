package com.realkarim.favourites.di

import com.realkarim.favourites.usecase.GetFavouriteCountriesUseCase
import com.realkarim.favourites.usecase.GetFavouriteCountriesUseCaseImpl
import com.realkarim.favourites.usecase.ObserveFavouriteStatusUseCase
import com.realkarim.favourites.usecase.ObserveFavouriteStatusUseCaseImpl
import com.realkarim.favourites.usecase.ToggleFavouriteUseCase
import com.realkarim.favourites.usecase.ToggleFavouriteUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesUseCaseModule {

    @Binds
    abstract fun bindToggleFavouriteUseCase(
        impl: ToggleFavouriteUseCaseImpl
    ): ToggleFavouriteUseCase

    @Binds
    abstract fun bindGetFavouriteCountriesUseCase(
        impl: GetFavouriteCountriesUseCaseImpl
    ): GetFavouriteCountriesUseCase

    @Binds
    abstract fun bindObserveFavouriteStatusUseCase(
        impl: ObserveFavouriteStatusUseCaseImpl
    ): ObserveFavouriteStatusUseCase
}
