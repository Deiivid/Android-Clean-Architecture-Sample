package com.example.rickymortydn.core.domain.usecase

import com.example.rickymortydn.core.domain.repository.EpisodeRepository
import com.example.rickymortydn.core.model.Episode
import com.example.rickymortydn.core.model.Page

class GetEpisodesUseCase(
    private val repository: EpisodeRepository,
) {
    suspend operator fun invoke(page: Int = 1): Result<Page<Episode>> =
        repository.getEpisodes(page)
}
