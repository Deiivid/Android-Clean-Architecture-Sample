package com.example.rickymortydn.core.data.network

import com.example.rickymortydn.core.data.network.model.CharacterDto
import com.example.rickymortydn.core.data.network.model.EpisodeDto
import com.example.rickymortydn.core.data.network.model.LocationDto
import com.example.rickymortydn.core.data.network.model.PagedResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): PagedResponseDto<CharacterDto>

    @GET("location")
    suspend fun getLocations(@Query("page") page: Int): PagedResponseDto<LocationDto>

    @GET("episode")
    suspend fun getEpisodes(@Query("page") page: Int): PagedResponseDto<EpisodeDto>
}
