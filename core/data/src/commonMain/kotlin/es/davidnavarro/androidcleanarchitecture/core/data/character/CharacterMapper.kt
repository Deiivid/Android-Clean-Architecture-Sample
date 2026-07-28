package es.davidnavarro.androidcleanarchitecture.core.data.character
import es.davidnavarro.androidcleanarchitecture.core.model.Character

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
    episodeCount = episodes.size
)
