package es.davidnavarro.androidcleanarchitecture.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PagedResponseDto<T>(val info: PageInfoDto, val results: List<T>)

@Serializable
internal data class PageInfoDto(val count: Int, val pages: Int)
