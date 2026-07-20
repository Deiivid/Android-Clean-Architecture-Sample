package com.example.rickymortydn.feature.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickymortydn.core.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharacters: GetCharactersUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun retry() = loadCharacters()

    fun loadNextPage() {
        val current = _uiState.value as? CharactersUiState.Content ?: return
        if (current.isLoadingMore || !current.canLoadMore) return

        _uiState.value = current.copy(isLoadingMore = true, loadMoreFailed = false)
        viewModelScope.launch {
            _uiState.value = getCharacters(current.page + 1).fold(
                onSuccess = { nextPage ->
                    current.copy(
                        characters = current.characters + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalCharacters = nextPage.totalItems,
                        isLoadingMore = false,
                        loadMoreFailed = false,
                    )
                },
                onFailure = {
                    current.copy(isLoadingMore = false, loadMoreFailed = true)
                },
            )
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = CharactersUiState.Loading
            _uiState.value = getCharacters(page = 1).fold(
                onSuccess = { page ->
                    if (page.items.isEmpty()) CharactersUiState.Empty
                    else CharactersUiState.Content(
                        characters = page.items,
                        page = page.number,
                        totalPages = page.totalPages,
                        totalCharacters = page.totalItems,
                    )
                },
                onFailure = CharactersUiState::Error,
            )
        }
    }
}
