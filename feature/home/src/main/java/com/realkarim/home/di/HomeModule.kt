package com.realkarim.home.di

import com.google.gson.Gson
import com.realkarim.data.service.CountryService
import com.realkarim.home.data.remote.HomeRemote
import com.realkarim.home.data.remote.HomeRemoteImpl
import com.realkarim.home.data.repository.HomeRepositoryImpl
import com.realkarim.home.domain.repository.HomeRepository
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCaseImpl
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
class HomeModule {
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
    fun provideHomeRemote(
        homeNetworkDataSource: NetworkDataSource<CountryService>
    ): HomeRemote {
        return HomeRemoteImpl(homeNetworkDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRemote: HomeRemote
    ): HomeRepository {
        return HomeRepositoryImpl(homeRemote)
    }

    @Provides
    fun provideGetPopularCountriesUseCase(
        homeRepository: HomeRepository
    ): GetPopularCountriesUseCase {
        return GetPopularCountriesUseCaseImpl(homeRepository)
    }
}