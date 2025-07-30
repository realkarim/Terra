package com.realkarim.data.di

import com.google.gson.Gson
import com.realkarim.data.service.CountryService
import com.realkarim.network.NetworkDataSource
import com.realkarim.network.ServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val BASE_URL = "https://www.apicountries.com/"

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideCountryServiceFactory(serviceFactory: ServiceFactory): CountryService {
        return serviceFactory.create(CountryService::class.java, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideNetworkDataSource(
        countryService: CountryService,
        gson: Gson,
    ): NetworkDataSource<CountryService> {
        return NetworkDataSource(countryService, gson)
    }
}