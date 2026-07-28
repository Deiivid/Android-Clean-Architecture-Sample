package es.davidnavarro.androidcleanarchitecture.core.domain.episode

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Page

interface EpisodeRepository {
    suspend fun getEpisodes(page: Int): CatalogResult<Page<Episode>, CatalogError>
}
