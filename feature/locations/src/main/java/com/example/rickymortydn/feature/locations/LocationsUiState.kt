package com.example.rickymortydn.feature.locations

import com.example.rickymortydn.core.model.Location

sealed interface LocationsUiState {
    data object Loading : LocationsUiState
    data object Empty : LocationsUiState
    data class Content(
        val locations: List<Location>,
        val page: Int,
        val totalPages: Int,
        val totalLocations: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false,
    ) : LocationsUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val cause: Throwable) : LocationsUiState
}
