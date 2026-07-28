package es.davidnavarro.androidcleanarchitecture.feature.locations

import es.davidnavarro.androidcleanarchitecture.core.domain.location.GetLocationsUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.location.LocationRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import es.davidnavarro.androidcleanarchitecture.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent

@OptIn(ExperimentalCoroutinesApi::class)
class LocationsViewModelTest {
    @Test
    fun `loads content on creation`() = runViewModelTest {
        val firstPage = page(location(), number = 1, totalPages = 7, totalItems = 126)
        val viewModel = viewModel(repository(1 to success(firstPage)))

        assertEquals(LocationsUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(
            LocationsUiState.Content(firstPage.items, 1, 7, 126),
            viewModel.uiState.value
        )
    }

    @Test
    fun `shows empty when the first page has no locations`() = runViewModelTest {
        val viewModel = viewModel(repository(1 to success(emptyPage())))

        runCurrent()

        assertEquals(LocationsUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `shows the typed error when the initial request fails`() = runViewModelTest {
        val error = CatalogError.Serialization
        val viewModel = viewModel(repository(1 to CatalogResult.Failure(error)))

        runCurrent()

        assertEquals(LocationsUiState.Error(error), viewModel.uiState.value)
    }

    @Test
    fun `retry requests page one again and recovers content`() = runViewModelTest {
        val firstPage = page(location())
        val repository = repository(
            1 to CatalogResult.Failure(CatalogError.Connectivity),
            1 to success(firstPage)
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.retry()
        runCurrent()

        assertEquals(listOf(1, 1), repository.requestedPages)
        assertEquals(LocationsUiState.Content(firstPage.items, 1, 2, 2), viewModel.uiState.value)
    }

    @Test
    fun `appends the next page`() = runViewModelTest {
        val first = location()
        val second = first.copy(id = 2, name = "Abadango")
        val repository = repository(
            1 to success(page(first)),
            2 to success(page(second, number = 2))
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        assertEquals(
            LocationsUiState.Content(listOf(first), 1, 2, 2, isLoadingMore = true),
            viewModel.uiState.value
        )
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            LocationsUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `rejects duplicate next page requests while one is running`() = runViewModelTest {
        val first = location()
        val second = first.copy(id = 2, name = "Abadango")
        val pendingPage = CompletableDeferred<CatalogResult<Page<Location>, CatalogError>>()
        val repository = FakeRepository { requestedPage ->
            if (requestedPage == 1) success(page(first)) else pendingPage.await()
        }
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        viewModel.loadNextPage()
        runCurrent()
        viewModel.loadNextPage()

        assertEquals(listOf(1, 2), repository.requestedPages)
        pendingPage.complete(success(page(second, number = 2)))
        runCurrent()
        assertEquals(
            LocationsUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `does not request another page after the last page`() = runViewModelTest {
        val repository = repository(
            1 to success(page(location(), totalPages = 1, totalItems = 1))
        )
        val viewModel = viewModel(repository)
        runCurrent()
        val finalState = viewModel.uiState.value

        viewModel.loadNextPage()
        runCurrent()

        assertEquals(listOf(1), repository.requestedPages)
        assertEquals(finalState, viewModel.uiState.value)
    }

    @Test
    fun `keeps content and exposes retry state when load more fails`() = runViewModelTest {
        val first = location()
        val repository = repository(
            1 to success(page(first)),
            2 to CatalogResult.Failure(CatalogError.Http(statusCode = 500))
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            LocationsUiState.Content(
                locations = listOf(first),
                page = 1,
                totalPages = 2,
                totalLocations = 2,
                loadMoreFailed = true
            ),
            viewModel.uiState.value
        )
    }

    private fun viewModel(repository: LocationRepository) = LocationsViewModel(GetLocationsUseCase(repository))

    private class FakeRepository(private val response: suspend (Int) -> CatalogResult<Page<Location>, CatalogError>) :
        LocationRepository {
        val requestedPages = mutableListOf<Int>()

        override suspend fun getLocations(page: Int): CatalogResult<Page<Location>, CatalogError> {
            requestedPages += page
            return response(page)
        }
    }

    private fun repository(vararg responses: Pair<Int, CatalogResult<Page<Location>, CatalogError>>): FakeRepository {
        val pendingResponses = ArrayDeque(responses.toList())
        return FakeRepository { requestedPage ->
            val (expectedPage, response) = pendingResponses.removeFirstOrNull()
                ?: error("Unexpected request for page $requestedPage")
            check(expectedPage == requestedPage) {
                "Expected page $expectedPage but requested $requestedPage"
            }
            response
        }
    }

    private fun success(page: Page<Location>): CatalogResult<Page<Location>, CatalogError> = CatalogResult.Success(page)

    private fun page(vararg locations: Location, number: Int = 1, totalPages: Int = 2, totalItems: Int = 2) = Page(
        items = locations.toList(),
        number = number,
        totalPages = totalPages,
        totalItems = totalItems
    )

    private fun emptyPage() = Page<Location>(emptyList(), 1, 1, 0)

    private fun location() = Location(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        residentCount = 27
    )
}
