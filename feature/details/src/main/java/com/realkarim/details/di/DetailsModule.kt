package com.realkarim.details.di

import com.realkarim.data.service.CountryService
import com.realkarim.details.data.remote.DetailsRemote
import com.realkarim.details.data.remote.DetailsRemoteImpl
import com.realkarim.details.data.repository.DetailsRepositoryImpl
import com.realkarim.details.domain.repository.DetailsRepository
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCase
import com.realkarim.details.domain.usecase.GetCountryDetailsUseCaseImpl
import com.realkarim.network.NetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DetailsModule {

    @Provides
    @Singleton
    fun provideDetailsRemote(
        detailsNetworkDataSource: NetworkDataSource<CountryService>
    ): DetailsRemote {
        return DetailsRemoteImpl(detailsNetworkDataSource)
    }

    @Provides
    @Singleton
    fun provideDetailsRepository(
        detailsRemote: DetailsRemote
    ): DetailsRepository {
        return DetailsRepositoryImpl(detailsRemote)
    }

    @Provides
    fun provideDetailsUseCase(
        detailsRepository: DetailsRepository
    ): GetCountryDetailsUseCase {
        return GetCountryDetailsUseCaseImpl(detailsRepository)
    }
}