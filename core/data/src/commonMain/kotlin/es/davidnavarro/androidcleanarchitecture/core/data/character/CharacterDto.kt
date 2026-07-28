package es.davidnavarro.androidcleanarchitecture.core.data.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: PlaceDto,
    val location: PlaceDto,
    @SerialName("image") val imageUrl: String,
    @SerialName("episode") val episodes: List<String>
)

@Serializable
internal data class PlaceDto(val name: String)
