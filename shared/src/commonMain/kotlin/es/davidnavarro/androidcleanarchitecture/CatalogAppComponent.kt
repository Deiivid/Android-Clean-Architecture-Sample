package es.davidnavarro.androidcleanarchitecture

import es.davidnavarro.androidcleanarchitecture.config.GeneratedCatalogConfiguration
import es.davidnavarro.androidcleanarchitecture.core.data.CatalogData
import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials
import es.davidnavarro.androidcleanarchitecture.core.data.config.NetworkConfiguration
import es.davidnavarro.androidcleanarchitecture.core.domain.character.GetCharactersUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.GetEpisodesUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.location.GetLocationsUseCase

class CatalogAppComponent internal constructor(
    private val data: CatalogData = CatalogData(
        configuration = NetworkConfiguration(GeneratedCatalogConfiguration.BASE_URL),
        credentials = ApiCredentials(
            apiKey = GeneratedCatalogConfiguration.API_KEY,
            clientId = GeneratedCatalogConfiguration.CLIENT_ID,
            tenantId = GeneratedCatalogConfiguration.TENANT_ID,
            accessToken = GeneratedCatalogConfiguration.ACCESS_TOKEN
        )
    )
) {
    internal val getCharacters = GetCharactersUseCase(data.characterRepository)
    internal val getLocations = GetLocationsUseCase(data.locationRepository)
    internal val getEpisodes = GetEpisodesUseCase(data.episodeRepository)

    internal fun close() {
        data.close()
    }
}
