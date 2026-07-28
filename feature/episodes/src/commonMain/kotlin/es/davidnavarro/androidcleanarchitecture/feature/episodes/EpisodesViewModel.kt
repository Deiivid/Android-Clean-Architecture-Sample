package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.GetEpisodesUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EpisodesViewModel(private val getEpisodes: GetEpisodesUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<EpisodesUiState>(EpisodesUiState.Loading)
    val uiState: StateFlow<EpisodesUiState> = _uiState.asStateFlow()

    init {
        loadEpisodes()
    }

    fun retry() = loadEpisodes()

    fun loadNextPage() {
        val current = _uiState.value as? EpisodesUiState.Content ?: return
        if (current.isLoadingMore || !current.canLoadMore) return

        _uiState.value = current.copy(isLoadingMore = true, loadMoreFailed = false)
        viewModelScope.launch {
            _uiState.value = when (val result = getEpisodes(current.page + 1)) {
                is CatalogResult.Success -> {
                    val nextPage = result.value
                    current.copy(
                        episodes = current.episodes + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalEpisodes = nextPage.totalItems,
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

    private fun loadEpisodes() {
        viewModelScope.launch {
            _uiState.value = EpisodesUiState.Loading
            _uiState.value = when (val result = getEpisodes(page = 1)) {
                is CatalogResult.Success -> {
                    val page = result.value
                    if (page.items.isEmpty()) {
                        EpisodesUiState.Empty
                    } else {
                        EpisodesUiState.Content(
                            episodes = page.items,
                            page = page.number,
                            totalPages = page.totalPages,
                            totalEpisodes = page.totalItems
                        )
                    }
                }
                is CatalogResult.Failure -> EpisodesUiState.Error(result.error)
            }
        }
    }
}
