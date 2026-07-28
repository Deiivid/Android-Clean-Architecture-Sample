package es.davidnavarro.androidcleanarchitecture.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

internal expect fun createPlatformHttpClient(configure: HttpClientConfig<*>.() -> Unit): HttpClient
