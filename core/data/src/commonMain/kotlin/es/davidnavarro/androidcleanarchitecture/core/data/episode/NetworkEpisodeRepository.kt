package es.davidnavarro.androidcleanarchitecture.core.data.episode

import es.davidnavarro.androidcleanarchitecture.core.data.mapper.toDomainPage
import es.davidnavarro.androidcleanarchitecture.core.data.network.RickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.repository.executeCatalogRequest
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.EpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Page

internal class NetworkEpisodeRepository(private val api: RickAndMortyApi) : EpisodeRepository {
    override suspend fun getEpisodes(page: Int): CatalogResult<Page<Episode>, CatalogError> = executeCatalogRequest {
        api.getEpisodes(page).toDomainPage(page) { it.toDomain() }
    }
}
