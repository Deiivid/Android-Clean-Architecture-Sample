package es.davidnavarro.androidcleanarchitecture.feature.episodes

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.model.Episode

sealed interface EpisodesUiState {
    data object Loading : EpisodesUiState
    data object Empty : EpisodesUiState
    data class Content(
        val episodes: List<Episode>,
        val page: Int,
        val totalPages: Int,
        val totalEpisodes: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false
    ) : EpisodesUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val error: CatalogError) : EpisodesUiState
}
