package es.davidnavarro.androidcleanarchitecture.core.data.location

import kotlinx.serialization.Serializable

@Serializable
internal data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>
)
