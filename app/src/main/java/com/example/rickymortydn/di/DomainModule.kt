package com.example.rickymortydn.di

import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.domain.repository.EpisodeRepository
import com.example.rickymortydn.core.domain.repository.LocationRepository
import com.example.rickymortydn.core.domain.usecase.GetCharactersUseCase
import com.example.rickymortydn.core.domain.usecase.GetEpisodesUseCase
import com.example.rickymortydn.core.domain.usecase.GetLocationsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideGetCharactersUseCase(
        repository: CharacterRepository,
    ): GetCharactersUseCase = GetCharactersUseCase(repository)

    @Provides
    fun provideGetEpisodesUseCase(
        repository: EpisodeRepository,
    ): GetEpisodesUseCase = GetEpisodesUseCase(repository)

    @Provides
    fun provideGetLocationsUseCase(
        repository: LocationRepository,
    ): GetLocationsUseCase = GetLocationsUseCase(repository)
}
