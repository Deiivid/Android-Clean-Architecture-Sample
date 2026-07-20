package com.example.rickymortydn.core.data.di

import com.example.rickymortydn.core.data.config.NetworkConfiguration
import com.example.rickymortydn.core.data.network.CredentialsInterceptor
import com.example.rickymortydn.core.data.network.RickAndMortyApi
import com.example.rickymortydn.core.data.repository.NetworkCharacterRepository
import com.example.rickymortydn.core.data.repository.NetworkEpisodeRepository
import com.example.rickymortydn.core.data.repository.NetworkLocationRepository
import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.domain.repository.EpisodeRepository
import com.example.rickymortydn.core.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindCharacterRepository(
        implementation: NetworkCharacterRepository,
    ): CharacterRepository

    @Binds
    abstract fun bindEpisodeRepository(
        implementation: NetworkEpisodeRepository,
    ): EpisodeRepository

    @Binds
    abstract fun bindLocationRepository(
        implementation: NetworkLocationRepository,
    ): LocationRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(
            credentialsInterceptor: CredentialsInterceptor,
        ): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(credentialsInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader("X-Api-Key")
                    redactHeader("X-Client-Id")
                    redactHeader("X-Tenant-Id")
                    redactHeader("Authorization")
                },
            )
            .build()

        @Provides
        @Singleton
        fun provideRickAndMortyApi(
            client: OkHttpClient,
            configuration: NetworkConfiguration,
        ): RickAndMortyApi = Retrofit.Builder()
            .baseUrl(configuration.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApi::class.java)
    }
}
