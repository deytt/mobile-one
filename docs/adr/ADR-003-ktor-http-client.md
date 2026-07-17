# ADR-003: Ktor como HTTP Client Compartilhado

**Status:** Aceito  
**Data:** 2026-07-17  
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

O aplicativo bancário realiza chamadas a diversas APIs:

- APIs proprietárias do banco (conta, saldo, extrato, transferências)
- APIs do Open Finance Brasil (consentimento, compartilhamento de dados)
- APIs do PIX (Banco Central — SPI)
- APIs de terceiros (análise de fraude, biometria facial para onboarding)

**Requisitos críticos do contexto bancário:**
- Certificate Pinning obrigatório (regulatório)
- Timeout configurável por tipo de operação
- Retry com backoff exponencial para operações idempotentes
- Logging de requisições para auditoria (sem dados sensíveis)
- Suporte a autenticação OAuth 2.0 / OpenID Connect (Open Finance)
- Intercepção para injeção de tokens JWT e refresh automático

A camada HTTP precisa estar no módulo `shared` (KMP).

---

## Decisão

Adotar **Ktor Client** como HTTP client no módulo `shared`.

---

## Alternativas Consideradas

### Alternativa A: Retrofit + OkHttp (Android only)

Retrofit é o padrão de facto no ecossistema Android, mas **não é compatível com KMP**. Adotá-lo exigiria duplicar toda a camada de networking — definições de API, interceptors, modelos — para Android e iOS separadamente.

**Descartada:** viola o princípio de não duplicar código de dados.

### Alternativa B: URLSession (iOS) + Retrofit (Android)

Manter soluções específicas por plataforma.

**Descartada:** duplicação de código, dois times mantendo lógicas equivalentes de autenticação, retry e certificate pinning.

### Alternativa C: Ktor Client (ESCOLHIDA)

Ktor é o HTTP client oficial da JetBrains para KMP, com engines nativas por plataforma (OkHttp no Android, Darwin no iOS). Mesma API, comportamento consistente, sem bridge.

---

## Configuração para contexto bancário

### Certificate Pinning

```kotlin
// shared/commonMain/data/remote/BankHttpClient.kt
val client = HttpClient(engine) {
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 30_000
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Auth) {
        bearer {
            loadTokens { BearerTokens(tokenStorage.accessToken, tokenStorage.refreshToken) }
            refreshTokens { /* lógica de refresh JWT */ }
        }
    }
    install(Logging) {
        logger = BankLogger // logger customizado que mascara dados sensíveis
        level = LogLevel.HEADERS // nunca LOG_BODY em produção
    }
}

// Certificate Pinning via expect/actual (engine específica)
expect fun createHttpEngine(certificatePins: List<String>): HttpClientEngine
```

```kotlin
// shared/androidMain
actual fun createHttpEngine(certificatePins: List<String>): HttpClientEngine =
    OkHttp.create {
        config {
            certificatePinner(
                CertificatePinner.Builder().apply {
                    certificatePins.forEach { add("*.banco.com.br", it) }
                }.build()
            )
        }
    }
```

```kotlin
// shared/iosMain
actual fun createHttpEngine(certificatePins: List<String>): HttpClientEngine =
    Darwin.create {
        configureSession {
            // TLS certificate validation customizada via URLSessionDelegate
        }
    }
```

### Retry com backoff

```kotlin
install(HttpRequestRetry) {
    retryOnServerErrors(maxRetries = 3)
    exponentialDelay()
    retryIf { _, response -> response.status == HttpStatusCode.TooManyRequests }
}
```

---

## Comparativo

| Critério | Ktor | Retrofit/OkHttp | URLSession |
|---|---|---|---|
| KMP | Sim | Não | Não |
| Certificate Pinning | Sim (via engine) | Sim (nativo) | Sim (nativo) |
| OAuth / JWT refresh | Plugin `Auth` | Interceptor | Manual |
| Retry / Timeout | Plugin nativo | Plugin nativo | Manual |
| Serialização | kotlinx.serialization | Gson/Moshi | Codable |
| Maturidade | GA, JetBrains | Altíssima | Altíssima |

---

## Consequências

### Positivas
- Certificate pinning, autenticação OAuth e retry configurados uma única vez, válidos para Android e iOS
- Comportamento de rede consistente entre plataformas — menos bugs de "funciona no Android mas não no iOS"
- Serialização via `kotlinx.serialization` — 100% KMP, type-safe

### Negativas / Trade-offs aceitos
- Ktor tem API diferente do Retrofit — Android devs precisam aprender a DSL do Ktor (curva pequena)
- Debugging de rede no iOS é menos ergonômico que no Android com OkHttp (pode usar Charles/Proxyman como alternativa)
- Algumas funcionalidades avançadas de OkHttp (ex: connection pool tuning) não têm equivalente direto

### Dependências introduzidas

```kotlin
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-client-auth = { module = "io.ktor:ktor-client-auth", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
```
