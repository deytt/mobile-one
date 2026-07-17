package com.mobileone.shared.data.remote

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class HttpClientFactorySmokeTest {

    private var client: io.ktor.client.HttpClient? = null

    @AfterTest
    fun tearDown() {
        client?.close()
    }

    @Test
    fun deveInstanciarHttpClientComEngineNativaDaPlataforma() {
        client = createHttpClient()
        assertNotNull(client)
    }
}
