package com.example.rickymortydn.core.data.mapper

import com.example.rickymortydn.core.data.network.model.CharacterDto
import com.example.rickymortydn.core.model.Character

internal fun CharacterDto.toDomain() = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin.name,
    location = location.name,
    imageUrl = imageUrl,
    episodeCount = episodes.size,
)
