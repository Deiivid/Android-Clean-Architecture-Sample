package es.davidnavarro.androidcleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.davidnavarro.androidcleanarchitecture.BuildConfig
import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials
import es.davidnavarro.androidcleanarchitecture.core.data.config.NetworkConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigurationModule {
    @Provides
    @Singleton
    fun provideNetworkConfiguration(): NetworkConfiguration = NetworkConfiguration(
        baseUrl = BuildConfig.RICK_AND_MORTY_BASE_URL
    )

    @Provides
    @Singleton
    fun provideApiCredentials(): ApiCredentials = ApiCredentials(
        apiKey = BuildConfig.RICK_AND_MORTY_API_KEY,
        clientId = BuildConfig.DEMO_CLIENT_ID,
        tenantId = BuildConfig.DEMO_TENANT_ID,
        accessToken = BuildConfig.DEMO_ACCESS_TOKEN
    )
}
