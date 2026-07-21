package es.davidnavarro.androidcleanarchitecture.core.data.location

import com.google.gson.annotations.SerializedName

internal data class LocationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("dimension") val dimension: String,
    @SerializedName("residents") val residents: List<String>
)
