package es.davidnavarro.androidcleanarchitecture.core.data.episode

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EpisodeDto(
    val id: Int,
    val name: String,
    @SerialName("air_date") val airDate: String,
    @SerialName("episode") val code: String,
    val characters: List<String>
)
