package es.davidnavarro.androidcleanarchitecture.feature.characters

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.model.Character

sealed interface CharactersUiState {
    data object Loading : CharactersUiState
    data object Empty : CharactersUiState
    data class Content(
        val characters: List<Character>,
        val page: Int,
        val totalPages: Int,
        val totalCharacters: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false
    ) : CharactersUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val error: CatalogError) : CharactersUiState
}
