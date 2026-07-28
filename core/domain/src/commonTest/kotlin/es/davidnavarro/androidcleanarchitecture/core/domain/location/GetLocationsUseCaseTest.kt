package es.davidnavarro.androidcleanarchitecture.core.domain.location
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetLocationsUseCaseTest {
    @Test
    fun `delegates location loading to repository`() = runTest {
        val expectedPage = Page(listOf(location()), number = 2, totalPages = 7, totalItems = 126)
        val repository = FakeLocationRepository(CatalogResult.Success(expectedPage))

        val result = GetLocationsUseCase(repository)(page = 2)

        assertEquals(CatalogResult.Success(expectedPage), result)
        assertEquals(2, repository.requestedPage)
    }

    private class FakeLocationRepository(private val result: CatalogResult<Page<Location>, CatalogError>) :
        LocationRepository {
        var requestedPage: Int? = null

        override suspend fun getLocations(page: Int): CatalogResult<Page<Location>, CatalogError> {
            requestedPage = page
            return result
        }
    }

    private fun location() = Location(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        residentCount = 27
    )
}
