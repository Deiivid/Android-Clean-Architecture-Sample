package com.example.rickymortydn.feature.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickymortydn.core.domain.usecase.GetEpisodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val getEpisodes: GetEpisodesUseCase,
) : ViewModel() {
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
            _uiState.value = getEpisodes(current.page + 1).fold(
                onSuccess = { nextPage ->
                    current.copy(
                        episodes = current.episodes + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalEpisodes = nextPage.totalItems,
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

    private fun loadEpisodes() {
        viewModelScope.launch {
            _uiState.value = EpisodesUiState.Loading
            _uiState.value = getEpisodes(page = 1).fold(
                onSuccess = { page ->
                    if (page.items.isEmpty()) EpisodesUiState.Empty
                    else EpisodesUiState.Content(
                        episodes = page.items,
                        page = page.number,
                        totalPages = page.totalPages,
                        totalEpisodes = page.totalItems,
                    )
                },
                onFailure = EpisodesUiState::Error,
            )
        }
    }
}
