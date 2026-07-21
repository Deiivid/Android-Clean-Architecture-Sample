package es.davidnavarro.androidcleanarchitecture.core.domain.location

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page

interface LocationRepository {
    suspend fun getLocations(page: Int): CatalogResult<Page<Location>, CatalogError>
}
