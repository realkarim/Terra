package com.realkarim.navigation.di

import com.realkarim.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {
    @Provides
    @Singleton
    fun provideNavigator(): Navigator = Navigator()
}