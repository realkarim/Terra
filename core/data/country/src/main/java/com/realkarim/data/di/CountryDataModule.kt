package com.realkarim.data.di

import com.google.gson.Gson
import com.realkarim.data.remote.CountryRemote
import com.realkarim.data.remote.CountryRemoteImpl
import com.realkarim.country.repository.CountryRepository
import com.realkarim.data.repository.CountryRepositoryImpl
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

    @Provides
    @Singleton
    fun provideCountryRemote(
        countryNetworkDataSource: NetworkDataSource<CountryService>
    ): CountryRemote {
        return CountryRemoteImpl(countryNetworkDataSource)
    }

    @Provides
    @Singleton
    fun provideCountryRepository(
        countryRemote: CountryRemote
    ): CountryRepository {
        return CountryRepositoryImpl(countryRemote)
    }
}