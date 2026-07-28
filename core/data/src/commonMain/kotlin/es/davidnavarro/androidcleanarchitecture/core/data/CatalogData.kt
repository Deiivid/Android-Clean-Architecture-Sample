package es.davidnavarro.androidcleanarchitecture.core.data

import es.davidnavarro.androidcleanarchitecture.core.data.character.NetworkCharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials
import es.davidnavarro.androidcleanarchitecture.core.data.config.NetworkConfiguration
import es.davidnavarro.androidcleanarchitecture.core.data.episode.NetworkEpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.data.location.NetworkLocationRepository
import es.davidnavarro.androidcleanarchitecture.core.data.network.KtorRickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.network.createPlatformHttpClient
import es.davidnavarro.androidcleanarchitecture.core.data.network.credentialHeaderNames
import es.davidnavarro.androidcleanarchitecture.core.data.network.toRequestHeaders
import es.davidnavarro.androidcleanarchitecture.core.domain.character.CharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.EpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.location.LocationRepository
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CatalogData(configuration: NetworkConfiguration, credentials: ApiCredentials) {
    private val client = createPlatformHttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.HEADERS
            sanitizeHeader { header -> header in credentialHeaderNames }
        }
        defaultRequest {
            credentials.toRequestHeaders().forEach { (name, value) ->
                header(name, value)
            }
        }
    }
    private val api = KtorRickAndMortyApi(client, configuration.baseUrl)

    val characterRepository: CharacterRepository = NetworkCharacterRepository(api)
    val episodeRepository: EpisodeRepository = NetworkEpisodeRepository(api)
    val locationRepository: LocationRepository = NetworkLocationRepository(api)

    fun close() {
        client.close()
    }
}
