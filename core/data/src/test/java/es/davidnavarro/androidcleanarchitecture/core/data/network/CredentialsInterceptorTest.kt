package es.davidnavarro.androidcleanarchitecture.core.data.network

import es.davidnavarro.androidcleanarchitecture.core.data.config.ApiCredentials
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CredentialsInterceptorTest {
    private val server = MockWebServer()

    @Before
    fun setUp() = server.start()

    @After
    fun tearDown() = server.close()

    @Test
    fun `adds configured credentials as request headers`() {
        server.enqueue(MockResponse())
        val credentials = ApiCredentials("api-key", "client", "tenant", "token")
        val client = OkHttpClient.Builder()
            .addInterceptor(CredentialsInterceptor(credentials))
            .build()

        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()

        with(server.takeRequest()) {
            assertEquals("api-key", headers["X-Api-Key"])
            assertEquals("client", headers["X-Client-Id"])
            assertEquals("tenant", headers["X-Tenant-Id"])
            assertEquals("Bearer token", headers["Authorization"])
        }
    }

    @Test
    fun `omits credential headers when values are blank`() {
        server.enqueue(MockResponse())
        val client = OkHttpClient.Builder()
            .addInterceptor(CredentialsInterceptor(ApiCredentials("", "", "", "")))
            .build()

        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()

        with(server.takeRequest()) {
            assertEquals(null, headers["X-Api-Key"])
            assertEquals(null, headers["X-Client-Id"])
            assertEquals(null, headers["X-Tenant-Id"])
            assertEquals(null, headers["Authorization"])
        }
    }
}
