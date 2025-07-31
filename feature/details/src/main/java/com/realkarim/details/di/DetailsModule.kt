package com.realkarim.details.di

import com.realkarim.country.repository.CountryRepository
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCase
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DetailsModule {
    @Provides
    fun provideDetailsUseCase(
        countryRepository: CountryRepository
    ): GetCountryDetailsUseCase {
        return GetCountryDetailsUseCaseImpl(countryRepository)
    }
}