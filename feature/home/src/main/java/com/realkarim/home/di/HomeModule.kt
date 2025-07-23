package com.realkarim.home.di

import com.google.gson.Gson
import com.realkarim.home.data.network.CountryService
import com.realkarim.home.data.remote.HomeRemote
import com.realkarim.home.data.repository.HomeRepositoryImpl
import com.realkarim.home.domain.repository.HomeRepository
import com.realkarim.network.NetworkDataSource
import com.realkarim.network.ServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

const val BASE_URL = "https://www.apicountries.com/"

@Module
@InstallIn(ViewModelComponent::class)
class HomeModule {
    @Provides
    @Singleton
    fun provideLoginServiceFactory(serviceFactory: ServiceFactory): CountryService {
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
    fun provideHomeRepository(
        homeRemote: HomeRemote
    ): HomeRepository {
        return HomeRepositoryImpl(homeRemote)
    }
}