package es.davidnavarro.androidcleanarchitecture.feature.locations

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.model.Location

sealed interface LocationsUiState {
    data object Loading : LocationsUiState
    data object Empty : LocationsUiState
    data class Content(
        val locations: List<Location>,
        val page: Int,
        val totalPages: Int,
        val totalLocations: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false
    ) : LocationsUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val error: CatalogError) : LocationsUiState
}
