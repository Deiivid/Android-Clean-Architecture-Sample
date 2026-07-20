package com.example.rickymortydn.feature.characters

import com.example.rickymortydn.core.model.Character

sealed interface CharactersUiState {
    data object Loading : CharactersUiState
    data object Empty : CharactersUiState
    data class Content(
        val characters: List<Character>,
        val page: Int,
        val totalPages: Int,
        val totalCharacters: Int,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false,
    ) : CharactersUiState {
        val canLoadMore: Boolean get() = page < totalPages
    }
    data class Error(val cause: Throwable) : CharactersUiState
}
