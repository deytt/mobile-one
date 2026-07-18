package com.mobileone.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

/**
 * Placeholder expect/actual para validar que o Ktor (ADR-003) instancia a engine nativa de
 * cada plataforma (OkHttp/Darwin). Certificate pinning, auth e retry entram com a SPEC-001.
 */
expect fun createHttpClientEngine(): HttpClientEngine

fun createHttpClient(engine: HttpClientEngine = createHttpClientEngine()): HttpClient =
    HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }
    }
