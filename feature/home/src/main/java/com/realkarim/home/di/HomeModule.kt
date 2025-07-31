package com.realkarim.home.di

import com.realkarim.country.repository.CountryRepository
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {
    @Provides
    fun provideGetPopularCountriesUseCase(
        countryRepository: CountryRepository
    ): GetPopularCountriesUseCase {
        return GetPopularCountriesUseCaseImpl(countryRepository)
    }
}