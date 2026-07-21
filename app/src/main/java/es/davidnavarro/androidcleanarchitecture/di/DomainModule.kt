package es.davidnavarro.androidcleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.davidnavarro.androidcleanarchitecture.core.domain.character.CharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.character.GetCharactersUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.EpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.GetEpisodesUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.location.GetLocationsUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.location.LocationRepository

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideGetCharactersUseCase(repository: CharacterRepository): GetCharactersUseCase =
        GetCharactersUseCase(repository)

    @Provides
    fun provideGetEpisodesUseCase(repository: EpisodeRepository): GetEpisodesUseCase = GetEpisodesUseCase(repository)

    @Provides
    fun provideGetLocationsUseCase(repository: LocationRepository): GetLocationsUseCase =
        GetLocationsUseCase(repository)
}
