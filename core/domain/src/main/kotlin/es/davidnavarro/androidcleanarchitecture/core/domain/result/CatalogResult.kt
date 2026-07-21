package es.davidnavarro.androidcleanarchitecture.core.domain.result

sealed interface CatalogResult<out T, out E> {
    data class Success<out T>(val value: T) : CatalogResult<T, Nothing>

    data class Failure<out E>(val error: E) : CatalogResult<Nothing, E>
}
