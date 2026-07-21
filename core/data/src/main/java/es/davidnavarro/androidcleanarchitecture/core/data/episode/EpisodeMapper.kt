package es.davidnavarro.androidcleanarchitecture.core.data.episode
import es.davidnavarro.androidcleanarchitecture.core.model.Episode

internal fun EpisodeDto.toDomain() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = code,
    characterCount = characters.size
)
