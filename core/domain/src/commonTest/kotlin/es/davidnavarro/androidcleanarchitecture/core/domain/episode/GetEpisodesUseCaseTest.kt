package es.davidnavarro.androidcleanarchitecture.core.domain.episode
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetEpisodesUseCaseTest {
    @Test
    fun `delegates episode loading to repository`() = runTest {
        val expectedPage = Page(listOf(episode()), number = 3, totalPages = 4, totalItems = 72)
        val repository = FakeEpisodeRepository(CatalogResult.Success(expectedPage))

        val result = GetEpisodesUseCase(repository)(page = 3)

        assertEquals(CatalogResult.Success(expectedPage), result)
        assertEquals(3, repository.requestedPage)
    }

    private class FakeEpisodeRepository(private val result: CatalogResult<Page<Episode>, CatalogError>) :
        EpisodeRepository {
        var requestedPage: Int? = null

        override suspend fun getEpisodes(page: Int): CatalogResult<Page<Episode>, CatalogError> {
            requestedPage = page
            return result
        }
    }

    private fun episode() = Episode(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        code = "S01E01",
        characterCount = 19
    )
}
