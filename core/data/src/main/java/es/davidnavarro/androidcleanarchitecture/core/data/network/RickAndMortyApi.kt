package es.davidnavarro.androidcleanarchitecture.core.data.network

import es.davidnavarro.androidcleanarchitecture.core.data.character.CharacterDto
import es.davidnavarro.androidcleanarchitecture.core.data.episode.EpisodeDto
import es.davidnavarro.androidcleanarchitecture.core.data.location.LocationDto
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PagedResponseDto
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
