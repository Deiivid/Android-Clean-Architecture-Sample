package es.davidnavarro.androidcleanarchitecture.core.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.davidnavarro.androidcleanarchitecture.core.data.character.NetworkCharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.data.config.NetworkConfiguration
import es.davidnavarro.androidcleanarchitecture.core.data.episode.NetworkEpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.data.location.NetworkLocationRepository
import es.davidnavarro.androidcleanarchitecture.core.data.network.CredentialsInterceptor
import es.davidnavarro.androidcleanarchitecture.core.data.network.RickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.domain.character.CharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.EpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.location.LocationRepository
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindCharacterRepository(implementation: NetworkCharacterRepository): CharacterRepository

    @Binds
    abstract fun bindEpisodeRepository(implementation: NetworkEpisodeRepository): EpisodeRepository

    @Binds
    abstract fun bindLocationRepository(implementation: NetworkLocationRepository): LocationRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(credentialsInterceptor: CredentialsInterceptor): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(credentialsInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader("X-Api-Key")
                    redactHeader("X-Client-Id")
                    redactHeader("X-Tenant-Id")
                    redactHeader("Authorization")
                }
            )
            .build()

        @Provides
        @Singleton
        fun provideRickAndMortyApi(client: OkHttpClient, configuration: NetworkConfiguration): RickAndMortyApi =
            Retrofit.Builder()
                .baseUrl(configuration.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RickAndMortyApi::class.java)
    }
}
