package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.davidnavarro.androidcleanarchitecture.core.domain.location.GetLocationsUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationsViewModel(private val getLocations: GetLocationsUseCase) : ViewModel() {
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
            _uiState.value = when (val result = getLocations(current.page + 1)) {
                is CatalogResult.Success -> {
                    val nextPage = result.value
                    current.copy(
                        locations = current.locations + nextPage.items,
                        page = nextPage.number,
                        totalPages = nextPage.totalPages,
                        totalLocations = nextPage.totalItems,
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

    private fun loadLocations() {
        viewModelScope.launch {
            _uiState.value = LocationsUiState.Loading
            _uiState.value = when (val result = getLocations(page = 1)) {
                is CatalogResult.Success -> {
                    val page = result.value
                    if (page.items.isEmpty()) {
                        LocationsUiState.Empty
                    } else {
                        LocationsUiState.Content(
                            locations = page.items,
                            page = page.number,
                            totalPages = page.totalPages,
                            totalLocations = page.totalItems
                        )
                    }
                }
                is CatalogResult.Failure -> LocationsUiState.Error(result.error)
            }
        }
    }
}
