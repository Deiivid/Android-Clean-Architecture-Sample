package com.example.rickymortydn.di

import com.example.rickymortydn.core.data.config.ApiCredentials
import com.example.rickymortydn.core.data.config.NetworkConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigurationModule {
    @Provides
    @Singleton
    fun provideNetworkConfiguration(): NetworkConfiguration = NetworkConfiguration(
        baseUrl = "https://rickandmortyapi.com/api/",
    )

    @Provides
    @Singleton
    fun provideApiCredentials(): ApiCredentials = ApiCredentials(
        apiKey = "",
        clientId = "",
        tenantId = "",
        accessToken = "",
    )
}
