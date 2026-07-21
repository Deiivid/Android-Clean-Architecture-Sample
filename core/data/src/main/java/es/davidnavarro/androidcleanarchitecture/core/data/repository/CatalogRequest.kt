package es.davidnavarro.androidcleanarchitecture.core.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import java.io.EOFException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

@Suppress("TooGenericExceptionCaught")
internal suspend inline fun <T> executeCatalogRequest(request: () -> T): CatalogResult<T, CatalogError> = try {
    CatalogResult.Success(request())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (exception: Exception) {
    CatalogResult.Failure(exception.toCatalogError())
}

private fun Exception.toCatalogError(): CatalogError = when {
    this is HttpException -> CatalogError.Http(statusCode = code())
    causeChain().any { it is JsonParseException || it is MalformedJsonException || it is EOFException } ->
        CatalogError.Serialization
    this is IOException -> CatalogError.Connectivity
    else -> CatalogError.Unexpected
}

private fun Throwable.causeChain(): Sequence<Throwable> =
    generateSequence(this) { current -> current.cause?.takeUnless { it === current } }
