package com.realkarim.country.di

import com.realkarim.country.usecase.GetCountryDetailsUseCase
import com.realkarim.country.usecase.GetCountryDetailsUseCaseImpl
import com.realkarim.country.usecase.GetAllCountriesUseCase
import com.realkarim.country.usecase.GetAllCountriesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CountryUseCaseModule {

    @Binds
    abstract fun bindGetAllCountriesUseCase(
        impl: GetAllCountriesUseCaseImpl
    ): GetAllCountriesUseCase

    @Binds
    abstract fun bindGetCountryDetailsUseCase(
        impl: GetCountryDetailsUseCaseImpl
    ): GetCountryDetailsUseCase
}
