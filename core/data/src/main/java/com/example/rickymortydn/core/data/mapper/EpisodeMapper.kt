package com.example.rickymortydn.core.data.mapper

import com.example.rickymortydn.core.data.network.model.EpisodeDto
import com.example.rickymortydn.core.model.Episode

internal fun EpisodeDto.toDomain() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = code,
    characterCount = characters.size,
)
