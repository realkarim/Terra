package com.realkarim.home.di

import com.realkarim.data.service.CountryService
import com.realkarim.home.data.remote.HomeRemote
import com.realkarim.home.data.remote.HomeRemoteImpl
import com.realkarim.home.data.repository.HomeRepositoryImpl
import com.realkarim.home.domain.repository.HomeRepository
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCase
import com.realkarim.home.domain.usecase.GetPopularCountriesUseCaseImpl
import com.realkarim.network.NetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {
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