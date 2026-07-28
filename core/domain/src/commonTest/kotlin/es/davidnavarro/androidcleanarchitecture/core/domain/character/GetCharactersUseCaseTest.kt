package es.davidnavarro.androidcleanarchitecture.core.domain.character
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlinx.coroutines.test.runTest

class GetCharactersUseCaseTest {
    @Test
    fun `delegates character loading to repository`() = runTest {
        val expected = Page(listOf(character()), number = 1, totalPages = 1, totalItems = 1)
        val repository = FakeCharacterRepository(CatalogResult.Success(expected))
        val useCase = GetCharactersUseCase(repository)

        assertEquals(CatalogResult.Success(expected), useCase(page = 2))
        assertEquals(2, repository.requestedPage)
    }

    @Test
    fun `returns repository error unchanged`() = runTest {
        val expected = CatalogError.Http(statusCode = 503)
        val useCase = GetCharactersUseCase(FakeCharacterRepository(CatalogResult.Failure(expected)))

        val result = useCase()

        assertSame(expected, (result as CatalogResult.Failure).error)
    }

    private class FakeCharacterRepository(private val result: CatalogResult<Page<Character>, CatalogError>) :
        CharacterRepository {
        var requestedPage: Int? = null

        override suspend fun getCharacters(page: Int): CatalogResult<Page<Character>, CatalogError> {
            requestedPage = page
            return result
        }
    }

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
