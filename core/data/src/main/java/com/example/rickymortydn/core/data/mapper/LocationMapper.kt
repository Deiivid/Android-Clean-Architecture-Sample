package com.example.rickymortydn.core.data.mapper

import com.example.rickymortydn.core.data.network.model.LocationDto
import com.example.rickymortydn.core.model.Location

internal fun LocationDto.toDomain() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentCount = residents.size,
)
