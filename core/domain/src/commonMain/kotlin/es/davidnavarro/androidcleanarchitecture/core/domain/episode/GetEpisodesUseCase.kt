package es.davidnavarro.androidcleanarchitecture.core.domain.episode
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Page

class GetEpisodesUseCase(private val repository: EpisodeRepository) {
    suspend operator fun invoke(page: Int = 1): CatalogResult<Page<Episode>, CatalogError> =
        repository.getEpisodes(page)
}
