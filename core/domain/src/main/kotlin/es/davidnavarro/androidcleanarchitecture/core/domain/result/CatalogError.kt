package es.davidnavarro.androidcleanarchitecture.core.domain.result

sealed interface CatalogError {
    data object Connectivity : CatalogError

    data class Http(val statusCode: Int) : CatalogError

    data object Serialization : CatalogError

    data object Unexpected : CatalogError
}
