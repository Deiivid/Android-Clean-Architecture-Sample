package es.davidnavarro.androidcleanarchitecture.core.data.location
import es.davidnavarro.androidcleanarchitecture.core.model.Location

internal fun LocationDto.toDomain() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentCount = residents.size
)
