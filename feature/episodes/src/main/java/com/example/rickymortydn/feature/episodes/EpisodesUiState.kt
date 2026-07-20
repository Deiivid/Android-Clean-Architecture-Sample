package com.example.rickymortydn.feature.episodes

import com.example.rickymortydn.core.model.Episode

sealed interface EpisodesUiState {
    data object Loading : EpisodesUiState
    data object Empty : EpisodesUiState
    data class Content(
        val episodes: List<Episode>,
        val page: Int,
        val totalPages: Int,
        val totalEpisodes: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false,
    ) : EpisodesUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val cause: Throwable) : EpisodesUiState
}
