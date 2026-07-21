package es.davidnavarro.androidcleanarchitecture.core.data.network.model

import com.google.gson.annotations.SerializedName

internal data class PagedResponseDto<T>(
    @SerializedName("info") val info: PageInfoDto,
    @SerializedName("results") val results: List<T>
)

internal data class PageInfoDto(@SerializedName("count") val count: Int, @SerializedName("pages") val pages: Int)
