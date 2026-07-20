package com.example.rickymortydn.feature.locations

import app.cash.turbine.test
import com.example.rickymortydn.core.domain.repository.LocationRepository
import com.example.rickymortydn.core.domain.usecase.GetLocationsUseCase
import com.example.rickymortydn.core.model.Location
import com.example.rickymortydn.core.model.Page
import com.example.rickymortydn.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LocationsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads locations on creation`() = runTest {
        val locations = listOf(Location(1, "Earth (C-137)", "Planet", "Dimension C-137", 27))
        val page = Page(locations, number = 1, totalPages = 7, totalItems = 126)
        val viewModel = LocationsViewModel(GetLocationsUseCase(FakeRepository(page)))

        viewModel.uiState.test {
            assertEquals(LocationsUiState.Loading, awaitItem())
            assertEquals(
                LocationsUiState.Content(locations, page = 1, totalPages = 7, totalLocations = 126),
                awaitItem(),
            )
        }
    }

    private class FakeRepository(
        private val page: Page<Location>,
    ) : LocationRepository {
        override suspend fun getLocations(page: Int): Result<Page<Location>> = Result.success(this.page)
    }
}
