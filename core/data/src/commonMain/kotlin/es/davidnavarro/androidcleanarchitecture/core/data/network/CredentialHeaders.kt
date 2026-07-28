package es.davidnavarro.androidcleanarchitecture.core.data.network

import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials

internal const val API_KEY_HEADER = "X-Api-Key"
internal const val CLIENT_ID_HEADER = "X-Client-Id"
internal const val TENANT_ID_HEADER = "X-Tenant-Id"
internal const val AUTHORIZATION_HEADER = "Authorization"

internal val credentialHeaderNames = setOf(
    API_KEY_HEADER,
    CLIENT_ID_HEADER,
    TENANT_ID_HEADER,
    AUTHORIZATION_HEADER
)

internal fun ApiCredentials.toRequestHeaders(): Map<String, String> = buildMap {
    apiKey.takeIf(String::isNotBlank)?.let { put(API_KEY_HEADER, it) }
    clientId.takeIf(String::isNotBlank)?.let { put(CLIENT_ID_HEADER, it) }
    tenantId.takeIf(String::isNotBlank)?.let { put(TENANT_ID_HEADER, it) }
    accessToken.takeIf(String::isNotBlank)?.let { put(AUTHORIZATION_HEADER, "Bearer $it") }
}
