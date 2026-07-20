package com.example.rickymortydn.core.data.repository

import com.example.rickymortydn.core.data.mapper.toDomain
import com.example.rickymortydn.core.data.mapper.toDomainPage
import com.example.rickymortydn.core.data.network.RickAndMortyApi
import com.example.rickymortydn.core.domain.repository.EpisodeRepository
import com.example.rickymortydn.core.model.Episode
import com.example.rickymortydn.core.model.Page
import javax.inject.Inject

internal class NetworkEpisodeRepository @Inject constructor(
    private val api: RickAndMortyApi,
) : EpisodeRepository {
    override suspend fun getEpisodes(page: Int): Result<Page<Episode>> = runCatching {
        api.getEpisodes(page).toDomainPage(page) { it.toDomain() }
    }
}
