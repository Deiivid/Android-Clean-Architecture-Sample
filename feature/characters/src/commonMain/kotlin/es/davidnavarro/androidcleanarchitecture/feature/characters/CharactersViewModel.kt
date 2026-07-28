package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.davidnavarro.androidcleanarchitecture.core.domain.character.GetCharactersUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharactersViewModel(private val getCharacters: GetCharactersUseCase) : ViewModel() {
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
            _uiState.value = when (val result = getCharacters(current.page + 1)) {
                is CatalogResult.Success -> {
                    val nextPage = result.value
                    current.copy(
                        characters = current.characters + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalCharacters = nextPage.totalItems,
                        isLoadingMore = false,
                        loadMoreFailed = false
                    )
                }
                is CatalogResult.Failure -> current.copy(
                    isLoadingMore = false,
                    loadMoreFailed = true
                )
            }
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = CharactersUiState.Loading
            _uiState.value = when (val result = getCharacters(page = 1)) {
                is CatalogResult.Success -> {
                    val page = result.value
                    if (page.items.isEmpty()) {
                        CharactersUiState.Empty
                    } else {
                        CharactersUiState.Content(
                            characters = page.items,
                            page = page.number,
                            totalPages = page.totalPages,
                            totalCharacters = page.totalItems
                        )
                    }
                }
                is CatalogResult.Failure -> CharactersUiState.Error(result.error)
            }
        }
    }
}
