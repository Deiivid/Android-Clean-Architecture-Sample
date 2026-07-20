package com.example.rickymortydn.core.data.network.model

import com.google.gson.annotations.SerializedName

internal data class CharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("type") val type: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("origin") val origin: PlaceDto,
    @SerializedName("location") val location: PlaceDto,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("episode") val episodes: List<String>,
)

internal data class PlaceDto(
    @SerializedName("name") val name: String,
)
