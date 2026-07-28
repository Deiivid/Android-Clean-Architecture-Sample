package es.davidnavarro.androidcleanarchitecture.core.data.network

import es.davidnavarro.androidcleanarchitecture.core.data.character.CharacterDto
import es.davidnavarro.androidcleanarchitecture.core.data.episode.EpisodeDto
import es.davidnavarro.androidcleanarchitecture.core.data.location.LocationDto
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PagedResponseDto
import es.davidnavarro.androidcleanarchitecture.core.data.repository.CatalogHttpException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

internal interface RickAndMortyApi {
    suspend fun getCharacters(page: Int): PagedResponseDto<CharacterDto>

    suspend fun getLocations(page: Int): PagedResponseDto<LocationDto>

    suspend fun getEpisodes(page: Int): PagedResponseDto<EpisodeDto>
}

internal class KtorRickAndMortyApi(private val client: HttpClient, baseUrl: String) : RickAndMortyApi {
    private val baseUrl = baseUrl.trimEnd('/')

    override suspend fun getCharacters(page: Int): PagedResponseDto<CharacterDto> =
        getPage(path = "character", page = page)

    override suspend fun getLocations(page: Int): PagedResponseDto<LocationDto> =
        getPage(path = "location", page = page)

    override suspend fun getEpisodes(page: Int): PagedResponseDto<EpisodeDto> = getPage(path = "episode", page = page)

    private suspend inline fun <reified T> getPage(path: String, page: Int): T {
        val response = client.get("$baseUrl/$path") {
            parameter("page", page)
        }
        if (!response.status.isSuccess()) {
            throw CatalogHttpException(response.status.value)
        }
        return response.body()
    }
}
