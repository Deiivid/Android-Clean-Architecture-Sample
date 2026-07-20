package com.example.rickymortydn.core.data.network

import com.example.rickymortydn.core.data.config.ApiCredentials
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CredentialsInterceptor @Inject constructor(
    private val credentials: ApiCredentials,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            credentials.apiKey.takeIf(String::isNotBlank)?.let { header("X-Api-Key", it) }
            credentials.clientId.takeIf(String::isNotBlank)?.let { header("X-Client-Id", it) }
            credentials.tenantId.takeIf(String::isNotBlank)?.let { header("X-Tenant-Id", it) }
            credentials.accessToken.takeIf(String::isNotBlank)?.let { header("Authorization", "Bearer $it") }
        }.build()

        return chain.proceed(request)
    }
}
