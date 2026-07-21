package es.davidnavarro.androidcleanarchitecture.feature.episodes

import es.davidnavarro.androidcleanarchitecture.core.domain.episode.EpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.episode.GetEpisodesUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import es.davidnavarro.androidcleanarchitecture.core.testing.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads content on creation`() = runTest {
        val firstPage = page(episode(), number = 1, totalPages = 3, totalItems = 51)
        val viewModel = viewModel(repository(1 to success(firstPage)))

        assertEquals(EpisodesUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(
            EpisodesUiState.Content(firstPage.items, 1, 3, 51),
            viewModel.uiState.value
        )
    }

    @Test
    fun `shows empty when the first page has no episodes`() = runTest {
        val viewModel = viewModel(repository(1 to success(emptyPage())))

        runCurrent()

        assertEquals(EpisodesUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `shows the typed error when the initial request fails`() = runTest {
        val error = CatalogError.Unexpected
        val viewModel = viewModel(repository(1 to CatalogResult.Failure(error)))

        runCurrent()

        assertEquals(EpisodesUiState.Error(error), viewModel.uiState.value)
    }

    @Test
    fun `retry requests page one again and recovers content`() = runTest {
        val firstPage = page(episode())
        val repository = repository(
            1 to CatalogResult.Failure(CatalogError.Connectivity),
            1 to success(firstPage)
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.retry()
        runCurrent()

        assertEquals(listOf(1, 1), repository.requestedPages)
        assertEquals(EpisodesUiState.Content(firstPage.items, 1, 2, 2), viewModel.uiState.value)
    }

    @Test
    fun `appends the next page`() = runTest {
        val first = episode()
        val second = first.copy(id = 2, name = "Lawnmower Dog", code = "S01E02")
        val repository = repository(
            1 to success(page(first)),
            2 to success(page(second, number = 2))
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        assertEquals(
            EpisodesUiState.Content(listOf(first), 1, 2, 2, isLoadingMore = true),
            viewModel.uiState.value
        )
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            EpisodesUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `rejects duplicate next page requests while one is running`() = runTest {
        val first = episode()
        val second = first.copy(id = 2, name = "Lawnmower Dog", code = "S01E02")
        val pendingPage = CompletableDeferred<CatalogResult<Page<Episode>, CatalogError>>()
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
            EpisodesUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `does not request another page after the last page`() = runTest {
        val repository = repository(
            1 to success(page(episode(), totalPages = 1, totalItems = 1))
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
    fun `keeps content and exposes retry state when load more fails`() = runTest {
        val first = episode()
        val repository = repository(
            1 to success(page(first)),
            2 to CatalogResult.Failure(CatalogError.Serialization)
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            EpisodesUiState.Content(
                episodes = listOf(first),
                page = 1,
                totalPages = 2,
                totalEpisodes = 2,
                loadMoreFailed = true
            ),
            viewModel.uiState.value
        )
    }

    private fun viewModel(repository: EpisodeRepository) = EpisodesViewModel(GetEpisodesUseCase(repository))

    private class FakeRepository(private val response: suspend (Int) -> CatalogResult<Page<Episode>, CatalogError>) :
        EpisodeRepository {
        val requestedPages = mutableListOf<Int>()

        override suspend fun getEpisodes(page: Int): CatalogResult<Page<Episode>, CatalogError> {
            requestedPages += page
            return response(page)
        }
    }

    private fun repository(vararg responses: Pair<Int, CatalogResult<Page<Episode>, CatalogError>>): FakeRepository {
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

    private fun success(page: Page<Episode>): CatalogResult<Page<Episode>, CatalogError> = CatalogResult.Success(page)

    private fun page(vararg episodes: Episode, number: Int = 1, totalPages: Int = 2, totalItems: Int = 2) = Page(
        items = episodes.toList(),
        number = number,
        totalPages = totalPages,
        totalItems = totalItems
    )

    private fun emptyPage() = Page<Episode>(emptyList(), 1, 1, 0)

    private fun episode() = Episode(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        code = "S01E01",
        characterCount = 19
    )
}
