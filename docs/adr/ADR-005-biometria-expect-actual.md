# ADR-005: Biometria e Segurança via Padrão expect/actual

**Status:** Aceito  
**Data:** 2026-07-17  
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

Um aplicativo bancário regulado pelo Banco Central do Brasil deve implementar:

- **Autenticação biométrica** — impressão digital e reconhecimento facial como fator de autenticação
- **Armazenamento seguro de credenciais** — tokens JWT, chaves criptográficas, PIN do usuário
- **Detecção de ambiente comprometido** — root (Android) e jailbreak (iOS)
- **Criptografia de dados sensíveis** — dados em trânsito (TLS + cert pinning) e em repouso (SQLCipher)

Todos esses recursos dependem de APIs nativas específicas de cada plataforma que **não podem ser abstraídas em Kotlin puro**. Ao mesmo tempo, a **lógica de quando usar cada recurso** (ex: quando pedir biometria, quantas tentativas antes de bloquear, o que fazer em ambiente comprometido) é regra de negócio — deve ficar no `shared`.

---

## Decisão

Usar o padrão **`expect/actual`** do KMP para separar a lógica de negócio (shared) da implementação nativa de segurança (androidMain / iosMain).

---

## Mapeamento de APIs de Segurança

| Funcionalidade | Android | iOS | Interface shared |
|---|---|---|---|
| Biometria | `BiometricPrompt` + `BiometricManager` | `LocalAuthentication` (Face ID / Touch ID) | `BiometricAuthenticator` |
| Armazenamento seguro | `Android Keystore System` | `Keychain` + `Secure Enclave` | `SecureStorage` |
| Root/Jailbreak | `RootBeer` + checagens nativas | Checagens de `/Applications/Cydia.app`, `fork()`, etc. | `DeviceIntegrityChecker` |
| Geração de chaves | `KeyPairGenerator` + `KeyGenParameterSpec` | `SecKeyCreateRandomKey` (Secure Enclave) | `CryptoKeyManager` |
| Hashing | `MessageDigest` | `CryptoKit` | `CryptoHasher` |

---

## Implementação do Padrão

### BiometricAuthenticator

```kotlin
// shared/commonMain/security/BiometricAuthenticator.kt
sealed class BiometricResult {
    object Success : BiometricResult()
    object UserCancelled : BiometricResult()
    object TooManyAttempts : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}

expect class BiometricAuthenticator {
    suspend fun isAvailable(): Boolean
    suspend fun authenticate(reason: String): BiometricResult
}

// shared/commonMain/usecase/AuthenticateWithBiometricUseCase.kt
class AuthenticateWithBiometricUseCase(
    private val biometricAuth: BiometricAuthenticator,
    private val sessionRepository: SessionRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(): Result<Unit> {
        if (!biometricAuth.isAvailable()) return Result.failure(BiometricNotAvailableError)

        return when (val result = biometricAuth.authenticate("Confirme sua identidade")) {
            is BiometricResult.Success -> {
                auditLogger.log(AuditEvent.BiometricSuccess)
                sessionRepository.extendSession()
                Result.success(Unit)
            }
            is BiometricResult.TooManyAttempts -> {
                auditLogger.log(AuditEvent.BiometricBlocked)
                sessionRepository.invalidateSession()
                Result.failure(BiometricBlockedError)
            }
            else -> Result.failure(BiometricFailedError)
        }
    }
}
```

```kotlin
// shared/androidMain/security/BiometricAuthenticator.android.kt
actual class BiometricAuthenticator(private val context: Context) {
    actual suspend fun isAvailable(): Boolean =
        BiometricManager.from(context)
            .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BIOMETRIC_SUCCESS

    actual suspend fun authenticate(reason: String): BiometricResult {
        // Implementação com BiometricPrompt e coroutines
    }
}
```

```kotlin
// shared/iosMain/security/BiometricAuthenticator.ios.kt
actual class BiometricAuthenticator {
    actual suspend fun isAvailable(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    }

    actual suspend fun authenticate(reason: String): BiometricResult {
        // Implementação com LocalAuthentication e suspendCoroutine
    }
}
```

### SecureStorage

```kotlin
// shared/commonMain/security/SecureStorage.kt
expect class SecureStorage {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun delete(key: String)
    suspend fun clear()
}

// Android actual: EncryptedSharedPreferences (Jetpack Security) + Keystore
// iOS actual: Keychain Services (kSecClassGenericPassword)
```

### DeviceIntegrityChecker

```kotlin
// shared/commonMain/security/DeviceIntegrityChecker.kt
data class IntegrityStatus(
    val isRooted: Boolean,
    val isEmulator: Boolean,
    val isDebuggable: Boolean
)

expect class DeviceIntegrityChecker {
    suspend fun check(): IntegrityStatus
}

// shared/commonMain/usecase/ValidateDeviceIntegrityUseCase.kt
class ValidateDeviceIntegrityUseCase(private val checker: DeviceIntegrityChecker) {
    suspend operator fun invoke(): Result<Unit> {
        val status = checker.check()
        return if (status.isRooted) {
            Result.failure(CompromisedDeviceError("Device root detected"))
        } else {
            Result.success(Unit)
        }
    }
}
```

---

## Benefício da Separação

A lógica de **quantas tentativas de biometria antes de bloquear**, **o que fazer após bloqueio** e **o que logar para auditoria** fica inteiramente no `shared`, testável com mocks sem necessidade de dispositivo físico.

Apenas o código de **"como chamar o sensor biométrico do hardware"** fica no `actual`.

Isso significa que um bug de segurança — ex: usuário não está sendo bloqueado após 5 tentativas — é **um único fix no shared**, não dois fixes em dois projetos.

---

## Conformidade Regulatória

| Requisito | Atendimento |
|---|---|
| Resolução BCB 85/2021 (autenticação forte) | BiometricAuthenticator com BIOMETRIC_STRONG |
| LGPD — dados biométricos não armazenados | Biometria apenas para desbloquear Keychain/Keystore, template não é salvo |
| PCI-DSS — dados de cartão nunca em texto plano | SecureStorage com encriptação em repouso |
| Detecção de fraude — ambiente comprometido | DeviceIntegrityChecker com bloqueio de acesso |

---

## Consequências

### Positivas
- Regras de segurança testáveis via unit test sem dispositivo físico
- Bug de lógica de autenticação corrigido em uma única base de código
- Conformidade regulatória auditável no código shared — único ponto de verificação

### Negativas / Trade-offs aceitos
- `expect/actual` com dependência de contexto Android (`Context`) requer injeção cuidadosa no Koin
- iOS: integração do KMP com callbacks do LocalAuthentication requer uso de `suspendCoroutine` — padrão menos comum para iOS devs
- Testes de integração de biometria exigem dispositivo físico ou simulador com biometria habilitada
