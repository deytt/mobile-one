# SPEC-001: Login com Autenticação Biométrica

**Status:** Aprovado  
**Versão:** 1.0  
**Data:** 2026-07-17  
**Feature owner:** Time Mobile  
**Figma:**
- Login: [`29:20015`](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-20015&m=dev)
- Biometria: [`29:20689`](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-20689&m=dev)
- Splash: [`28:19512`](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=28-19512&m=dev)

---

## Objetivo

Implementar o fluxo de autenticação do usuário com suporte a senha e biometria (impressão digital / Face ID), demonstrando que regras de negócio complexas de autenticação bancária podem ser compartilhadas entre Android e iOS via KMP.

---

## Escopo da POC

Esta feature demonstra especificamente:
- ✅ Use case de autenticação rodando em Kotlin compartilhado
- ✅ Biometria via `expect/actual` (implementação nativa, lógica compartilhada)
- ✅ Armazenamento seguro de sessão via `SecureStorage` (expect/actual)
- ✅ UI nativa em Compose (Android) e SwiftUI (iOS)
- ✅ Mesmo fluxo de estado (`UiState`) consumido pelas duas plataformas

---

## Fluxo de usuário

```
[Abertura do app]
        │
        ├──[Primeira vez / sem sessão]──→ [Tela de Login com CPF + Senha]
        │                                          │
        │                                          ├─[Sucesso]──→ [Configurar biometria?]──→ [Home]
        │                                          └─[Erro]──→ [Exibir erro + contador tentativas]
        │
        └──[Sessão salva + biometria configurada]──→ [Tela de Boas-vindas + Biometria]
                                                              │
                                                              ├─[Sucesso biometria]──→ [Home]
                                                              ├─[Cancelou]──→ [Tela de Login com CPF + Senha]
                                                              └─[5 falhas]──→ [Conta bloqueada temporariamente]
```

---

## Contratos do shared (KMP)

### Estado de UI

```kotlin
// shared/commonMain/feature/auth/AuthUiState.kt
data class AuthUiState(
    val isLoading: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val failedAttempts: Int = 0,
    val isAccountLocked: Boolean = false,
    val lockRemainingSeconds: Int = 0,
    val error: AuthError? = null,
    val navigation: AuthNavigation? = null
)

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object AccountLocked : AuthError()
    object BiometricFailed : AuthError()
    object NetworkError : AuthError()
    data class UnknownError(val message: String) : AuthError()
}

sealed class AuthNavigation {
    object ToHome : AuthNavigation()
    object ToBiometricSetup : AuthNavigation()
}
```

### Use Cases

```kotlin
// 1. Login com credenciais
class LoginWithCredentialsUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val deviceIntegrity: ValidateDeviceIntegrityUseCase
) {
    suspend operator fun invoke(cpf: String, password: String): Result<AuthToken>
}

// 2. Login com biometria
class LoginWithBiometricUseCase(
    private val biometricAuth: BiometricAuthenticator,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<AuthToken>
}

// 3. Configurar biometria
class SetupBiometricLoginUseCase(
    private val biometricAuth: BiometricAuthenticator,
    private val secureStorage: SecureStorage
) {
    suspend operator fun invoke(): Result<Unit>
}
```

### Repositório (interface)

```kotlin
interface AuthRepository {
    suspend fun login(cpf: String, password: String): Result<AuthToken>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
}
```

---

## Validações (no shared — domínio)

| Campo | Regra | Erro |
|---|---|---|
| CPF | 11 dígitos, algoritmo validador de CPF | `ValidationError("cpf", "CPF inválido")` |
| Senha | 6–20 caracteres, ao menos 1 número | `ValidationError("password", "Senha fraca")` |
| Tentativas | Máximo 5 antes do bloqueio | `AccountLocked` |
| Bloqueio | 5 minutos — countdown exibido na UI | `lockRemainingSeconds` no estado |

---

## Requisitos de UI

### Tela 1: Login com Senha

| Elemento | Detalhe |
|---|---|
| Logo do banco | Carregado via `WhiteLabelConfig.logoUrl` |
| Campo CPF | Máscara `###.###.###-##`, teclado numérico |
| Campo Senha | Ocultado por padrão, botão de reveal |
| Botão entrar | Desabilitado se campos inválidos |
| Link "Esqueci minha senha" | Fora do escopo da POC |
| Botão biometria | Exibido apenas se `isBiometricEnabled == true` |

### Tela 2: Boas-vindas com Biometria

| Elemento | Detalhe |
|---|---|
| "Olá, [nome do usuário]" | Nome carregado do `SecureStorage` |
| Ícone biométrico | Impressão digital (Android) / Face ID (iOS) — nativo |
| Botão "Usar senha" | Navega para Tela 1 |
| Prompt biométrico | Acionado automaticamente ao abrir a tela |

---

## Testes requeridos (shared)

```kotlin
// LoginWithCredentialsUseCaseTest.kt — roda em Android E iOS
class LoginWithCredentialsUseCaseTest {
    @Test fun `deve retornar sucesso com credenciais válidas`()
    @Test fun `deve retornar InvalidCredentials com senha errada`()
    @Test fun `deve bloquear conta após 5 tentativas`()
    @Test fun `deve falhar se CPF inválido`()
    @Test fun `deve falhar se dispositivo comprometido (root)`()
}
```

---

## Critérios de aceite da POC

- [ ] Login com CPF + senha funciona em Android e iOS usando o mesmo use case
- [ ] Biometria funciona no Android (fingerprint) e iOS (Face ID) com lógica de tentativas no shared
- [ ] Conta bloqueia após 5 tentativas com countdown de 5 minutos (lógica no shared, UI nativa)
- [ ] Trocar o `WhiteLabelConfig` muda o logo sem alterar código de UI
- [ ] Os testes do use case rodam no Android runner e no iOS runner do KMP
