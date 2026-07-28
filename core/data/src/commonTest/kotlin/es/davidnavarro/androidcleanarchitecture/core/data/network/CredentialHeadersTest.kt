package es.davidnavarro.androidcleanarchitecture.core.data.network

import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials
import kotlin.test.Test
import kotlin.test.assertEquals

class CredentialHeadersTest {
    @Test
    fun `maps configured credentials to request headers`() {
        val headers = ApiCredentials(
            apiKey = "api-key",
            clientId = "client",
            tenantId = "tenant",
            accessToken = "token"
        ).toRequestHeaders()

        assertEquals(
            mapOf(
                API_KEY_HEADER to "api-key",
                CLIENT_ID_HEADER to "client",
                TENANT_ID_HEADER to "tenant",
                AUTHORIZATION_HEADER to "Bearer token"
            ),
            headers
        )
    }

    @Test
    fun `omits credential headers when values are blank`() {
        val headers = ApiCredentials("", "", "", "").toRequestHeaders()

        assertEquals(emptyMap(), headers)
    }

    @Test
    fun `marks every credential header as sensitive`() {
        assertEquals(
            setOf(
                API_KEY_HEADER,
                CLIENT_ID_HEADER,
                TENANT_ID_HEADER,
                AUTHORIZATION_HEADER
            ),
            credentialHeaderNames
        )
    }
}
