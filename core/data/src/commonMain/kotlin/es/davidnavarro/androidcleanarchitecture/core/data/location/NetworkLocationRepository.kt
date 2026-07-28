package es.davidnavarro.androidcleanarchitecture.core.data.location

import es.davidnavarro.androidcleanarchitecture.core.data.mapper.toDomainPage
import es.davidnavarro.androidcleanarchitecture.core.data.network.RickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.repository.executeCatalogRequest
import es.davidnavarro.androidcleanarchitecture.core.domain.location.LocationRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page

internal class NetworkLocationRepository(private val api: RickAndMortyApi) : LocationRepository {
    override suspend fun getLocations(page: Int): CatalogResult<Page<Location>, CatalogError> = executeCatalogRequest {
        api.getLocations(page).toDomainPage(page) { it.toDomain() }
    }
}
