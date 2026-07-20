package com.example.rickymortydn.core.domain.repository

import com.example.rickymortydn.core.model.Episode
import com.example.rickymortydn.core.model.Page

interface EpisodeRepository {
    suspend fun getEpisodes(page: Int): Result<Page<Episode>>
}
