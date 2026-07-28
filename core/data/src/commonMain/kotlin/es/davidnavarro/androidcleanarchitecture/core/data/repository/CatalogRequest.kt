package es.davidnavarro.androidcleanarchitecture.core.data.repository

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

@Suppress("TooGenericExceptionCaught")
internal suspend inline fun <T> executeCatalogRequest(request: () -> T): CatalogResult<T, CatalogError> = try {
    CatalogResult.Success(request())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (exception: Exception) {
    CatalogResult.Failure(exception.toCatalogError())
}

private fun Exception.toCatalogError(): CatalogError = when {
    this is CatalogHttpException -> CatalogError.Http(statusCode = statusCode)
    causeChain().any { it is SerializationException } ->
        CatalogError.Serialization
    this is IOException -> CatalogError.Connectivity
    else -> CatalogError.Unexpected
}

private fun Throwable.causeChain(): Sequence<Throwable> =
    generateSequence(this) { current -> current.cause?.takeUnless { it === current } }

internal class CatalogHttpException(val statusCode: Int) : Exception()
