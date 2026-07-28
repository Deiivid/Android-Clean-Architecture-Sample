package es.davidnavarro.androidcleanarchitecture.feature.characters

import es.davidnavarro.androidcleanarchitecture.core.domain.character.CharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.character.GetCharactersUseCase
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import es.davidnavarro.androidcleanarchitecture.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {
    @Test
    fun `loads content on creation`() = runViewModelTest {
        val firstPage = page(character(), number = 1, totalPages = 42, totalItems = 826)
        val viewModel = viewModel(repository(1 to success(firstPage)))

        assertEquals(CharactersUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(
            CharactersUiState.Content(
                characters = firstPage.items,
                page = 1,
                totalPages = 42,
                totalCharacters = 826
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `shows empty when the first page has no characters`() = runViewModelTest {
        val viewModel = viewModel(repository(1 to success(emptyPage())))

        runCurrent()

        assertEquals(CharactersUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `shows the typed error when the initial request fails`() = runViewModelTest {
        val error = CatalogError.Http(statusCode = 503)
        val viewModel = viewModel(repository(1 to CatalogResult.Failure(error)))

        runCurrent()

        assertEquals(CharactersUiState.Error(error), viewModel.uiState.value)
    }

    @Test
    fun `retry requests page one again and recovers content`() = runViewModelTest {
        val firstPage = page(character())
        val repository = repository(
            1 to CatalogResult.Failure(CatalogError.Connectivity),
            1 to success(firstPage)
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.retry()
        runCurrent()

        assertEquals(listOf(1, 1), repository.requestedPages)
        assertEquals(
            CharactersUiState.Content(firstPage.items, 1, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `appends the next page`() = runViewModelTest {
        val first = character()
        val second = first.copy(id = 2, name = "Morty Smith")
        val repository = repository(
            1 to success(page(first)),
            2 to success(page(second, number = 2))
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        assertEquals(
            CharactersUiState.Content(listOf(first), 1, 2, 2, isLoadingMore = true),
            viewModel.uiState.value
        )
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            CharactersUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `rejects duplicate next page requests while one is running`() = runViewModelTest {
        val first = character()
        val second = first.copy(id = 2, name = "Morty Smith")
        val pendingPage = CompletableDeferred<CatalogResult<Page<Character>, CatalogError>>()
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
            CharactersUiState.Content(listOf(first, second), 2, 2, 2),
            viewModel.uiState.value
        )
    }

    @Test
    fun `does not request another page after the last page`() = runViewModelTest {
        val repository = repository(
            1 to success(page(character(), totalPages = 1, totalItems = 1))
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
        val first = character()
        val repository = repository(
            1 to success(page(first)),
            2 to CatalogResult.Failure(CatalogError.Connectivity)
        )
        val viewModel = viewModel(repository)
        runCurrent()

        viewModel.loadNextPage()
        runCurrent()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(
            CharactersUiState.Content(
                characters = listOf(first),
                page = 1,
                totalPages = 2,
                totalCharacters = 2,
                loadMoreFailed = true
            ),
            viewModel.uiState.value
        )
    }

    private fun viewModel(repository: CharacterRepository) = CharactersViewModel(GetCharactersUseCase(repository))

    private class FakeRepository(private val response: suspend (Int) -> CatalogResult<Page<Character>, CatalogError>) :
        CharacterRepository {
        val requestedPages = mutableListOf<Int>()

        override suspend fun getCharacters(page: Int): CatalogResult<Page<Character>, CatalogError> {
            requestedPages += page
            return response(page)
        }
    }

    private fun repository(vararg responses: Pair<Int, CatalogResult<Page<Character>, CatalogError>>): FakeRepository {
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

    private fun success(page: Page<Character>): CatalogResult<Page<Character>, CatalogError> =
        CatalogResult.Success(page)

    private fun page(vararg characters: Character, number: Int = 1, totalPages: Int = 2, totalItems: Int = 2) = Page(
        items = characters.toList(),
        number = number,
        totalPages = totalPages,
        totalItems = totalItems
    )

    private fun emptyPage() = Page<Character>(
        items = emptyList(),
        number = 1,
        totalPages = 1,
        totalItems = 0
    )

    private fun character() = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth",
        location = "Citadel of Ricks",
        imageUrl = "https://example.com/rick.png",
        episodeCount = 51
    )
}
