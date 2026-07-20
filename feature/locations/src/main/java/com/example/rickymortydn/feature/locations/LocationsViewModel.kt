package com.example.rickymortydn.feature.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickymortydn.core.domain.usecase.GetLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val getLocations: GetLocationsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LocationsUiState>(LocationsUiState.Loading)
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    fun retry() = loadLocations()

    fun loadNextPage() {
        val current = _uiState.value as? LocationsUiState.Content ?: return
        if (current.isLoadingMore || !current.canLoadMore) return

        _uiState.value = current.copy(isLoadingMore = true, loadMoreFailed = false)
        viewModelScope.launch {
            _uiState.value = getLocations(current.page + 1).fold(
                onSuccess = { nextPage ->
                    current.copy(
                        locations = current.locations + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalLocations = nextPage.totalItems,
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

    private fun loadLocations() {
        viewModelScope.launch {
            _uiState.value = LocationsUiState.Loading
            _uiState.value = getLocations(page = 1).fold(
                onSuccess = { page ->
                    if (page.items.isEmpty()) LocationsUiState.Empty
                    else LocationsUiState.Content(
                        locations = page.items,
                        page = page.number,
                        totalPages = page.totalPages,
                        totalLocations = page.totalItems,
                    )
                },
                onFailure = LocationsUiState::Error,
            )
        }
    }
}
