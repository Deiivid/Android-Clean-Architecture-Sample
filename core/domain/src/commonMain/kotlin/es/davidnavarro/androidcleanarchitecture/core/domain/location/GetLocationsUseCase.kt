package es.davidnavarro.androidcleanarchitecture.core.domain.location
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page

class GetLocationsUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(page: Int = 1): CatalogResult<Page<Location>, CatalogError> =
        repository.getLocations(page)
}
