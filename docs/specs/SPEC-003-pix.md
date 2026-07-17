# SPEC-003: Transferência PIX

**Status:** Aprovado  
**Versão:** 1.0  
**Data:** 2026-07-17  
**Feature owner:** Time Mobile  
**Figma:**
- Fluxo PIX completo (8 telas): [`15:9848`](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=15-9848&m=dev)

---

## Objetivo

Implementar o fluxo completo de transferência PIX — da inserção da chave até a confirmação com biometria — demonstrando que **regras de negócio complexas de Open Finance e regulatórias** ficam no shared KMP, reutilizadas identicamente entre Android e iOS.

Esta é a feature de maior impacto para convencer gestores, pois demonstra:
1. Validadores de chave PIX compartilhados (regra regulatória do Banco Central)
2. Consulta ao diretório DICT do Banco Central via Ktor compartilhado
3. Confirmação de transação com biometria (expect/actual)
4. Comprovante gerado no shared, exibido nativamente

---

## Tipos de chave PIX suportados

| Tipo | Formato | Validação |
|---|---|---|
| CPF | `###.###.###-##` | Algoritmo CPF |
| CNPJ | `##.###.###/####-##` | Algoritmo CNPJ |
| Telefone | `+55 (##) #####-####` | DDD válido + 9 dígitos |
| E-mail | RFC 5322 simplificado | Regex padrão |
| Chave aleatória | UUID v4 | 36 caracteres, formato UUID |
| QR Code | Payload EMV | Parser do padrão PIX (BCB) |

---

## Fluxo de usuário

```
[Atalho PIX na Home]
        │
        └──→ [Tela: Qual é a chave PIX?]
                        │
                        ├─[Digitar chave]──→ [Detecção automática do tipo]──→ [Validação em tempo real]
                        │
                        ├─[Escanear QR Code]──→ [Câmera nativa]──→ [Parser no shared]──→ [Pré-preenchimento]
                        │
                        └─[Chave válida + Botão Continuar]
                                    │
                                    └──→ [Consulta ao DICT (Ktor)]──→ [Tela: Confirmar destinatário]
                                                                                │
                                                                                └──→ [Tela: Digite o valor]
                                                                                            │
                                                                                            └──→ [Tela: Revisão]
                                                                                                        │
                                                                                                        └──→ [Biometria]
                                                                                                                    │
                                                                                                                    ├─[✓]──→ [Processando]──→ [Comprovante]
                                                                                                                    └─[✗]──→ [Cancelado]
```

---

## Contratos do shared (KMP)

### Estado de UI (máquina de estados do fluxo)

```kotlin
// shared/commonMain/feature/pix/PixTransferUiState.kt
data class PixTransferUiState(
    val step: PixStep = PixStep.EnterKey,
    val keyInput: String = "",
    val detectedKeyType: PixKeyType? = null,
    val keyValidation: PixKeyValidation = PixKeyValidation.Idle,
    val recipient: RecipientDisplay? = null,
    val amount: Long = 0L,                      // centavos
    val amountFormatted: String = "R$ 0,00",
    val description: String = "",
    val isLoading: Boolean = false,
    val receipt: PixReceipt? = null,
    val error: PixError? = null
)

enum class PixStep { EnterKey, ConfirmRecipient, EnterAmount, Review, Processing, Receipt }

sealed class PixKeyType {
    object CPF : PixKeyType()
    object CNPJ : PixKeyType()
    object Phone : PixKeyType()
    object Email : PixKeyType()
    object RandomKey : PixKeyType()
    object QRCode : PixKeyType()
}

sealed class PixKeyValidation {
    object Idle : PixKeyValidation()
    object Valid : PixKeyValidation()
    data class Invalid(val reason: String) : PixKeyValidation()
}

data class RecipientDisplay(
    val name: String,
    val institution: String,
    val maskedKey: String           // "•••.456.•••-••" para CPF
)

data class PixReceipt(
    val transactionId: String,
    val e2eId: String,              // End-to-End ID do Banco Central
    val recipientName: String,
    val amountFormatted: String,
    val dateTimeFormatted: String,
    val authenticationCode: String
)
```

### Use Cases

```kotlin
// 1. Detectar tipo da chave em tempo real (puro, sem IO)
class DetectPixKeyTypeUseCase {
    operator fun invoke(input: String): PixKeyType?
}

// 2. Validar chave PIX (puro, sem IO)
class ValidatePixKeyUseCase {
    operator fun invoke(key: String, type: PixKeyType): Result<Unit>
}

// 3. Consultar destinatário no DICT (Banco Central)
class LookupPixRecipientUseCase(private val pixRepository: PixRepository) {
    suspend operator fun invoke(key: String): Result<Recipient>
}

// 4. Executar transferência PIX
class ExecutePixTransferUseCase(
    private val pixRepository: PixRepository,
    private val biometricAuth: BiometricAuthenticator,
    private val sessionRepository: SessionRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(transfer: PixTransferRequest): Result<PixReceipt>
}

// 5. Parser de QR Code PIX (padrão EMV do BCB)
class ParsePixQRCodeUseCase {
    operator fun invoke(rawPayload: String): Result<PixQRCodeData>
}
```

### Limites de transferência (regras de negócio no shared)

```kotlin
// shared/commonMain/feature/pix/PixLimitsValidator.kt
class PixLimitsValidator {
    fun validate(amountCents: Long, period: PixPeriod): Result<Unit> {
        val limit = when (period) {
            PixPeriod.DAY -> 20_000_00L       // R$ 20.000,00 diurno
            PixPeriod.NIGHT -> 1_000_00L      // R$ 1.000,00 noturno (21h–6h)
        }
        return if (amountCents > limit)
            Result.failure(PixLimitExceededError(limit))
        else
            Result.success(Unit)
    }
}
```

---

## Validadores de chave (no shared — 100% compartilhado)

```kotlin
// Exemplo: validador de CPF
object CpfValidator {
    fun validate(cpf: String): Boolean {
        val digits = cpf.filter { it.isDigit() }
        if (digits.length != 11) return false
        if (digits.all { it == digits[0] }) return false
        // Algoritmo de validação dos dígitos verificadores
        val d1 = calculateDigit(digits.take(9))
        val d2 = calculateDigit(digits.take(10))
        return digits[9].digitToInt() == d1 && digits[10].digitToInt() == d2
    }
}
```

---

## Câmera para QR Code (expect/actual)

```kotlin
// shared/commonMain
expect class QRCodeScanner {
    suspend fun scan(): Result<String>
}

// androidMain: ML Kit ou ZXing
// iosMain: AVFoundation + Vision framework
```

A lógica de **o que fazer com o payload** (parsing, validação, extração de dados) fica no shared via `ParsePixQRCodeUseCase`.

---

## Requisitos de UI

| Tela | Elementos chave |
|---|---|
| Inserir chave | Campo único, detecção automática do tipo, ícone do tipo detectado, botão câmera |
| Confirmar destinatário | Card com nome, banco, chave mascarada — botão "Não é esse?" |
| Inserir valor | Teclado numérico grande, limite exibido, campo descrição opcional |
| Revisão | Resumo completo, destaque no valor, botão "Confirmar com biometria" |
| Processando | Animação de loading (não bloqueante, com timeout de 30s) |
| Comprovante | E2E ID, valor, destinatário, data/hora, botão compartilhar (nativo) |

---

## Testes requeridos (shared)

```kotlin
class ValidatePixKeyUseCaseTest {
    @Test fun `deve validar CPF correto`()
    @Test fun `deve rejeitar CPF com dígitos iguais`()
    @Test fun `deve validar chave aleatória no formato UUID`()
    @Test fun `deve detectar tipo Email pelo arroba`()
    @Test fun `deve rejeitar valor acima do limite noturno após 21h`()
}

class ParsePixQRCodeUseCaseTest {
    @Test fun `deve parsear payload EMV estático`()
    @Test fun `deve parsear payload EMV dinâmico com valor`()
    @Test fun `deve rejeitar payload inválido`()
}
```

---

## Critérios de aceite da POC

- [ ] Validação de CPF/CNPJ/Email/Telefone/UUID funcionando em Android e iOS (mesmo código)
- [ ] Consulta ao DICT retorna destinatário (mock ou sandbox do BCB)
- [ ] Limite diurno/noturno validado no shared com feedback correto na UI
- [ ] Confirmação com biometria antes de enviar
- [ ] Comprovante exibido com E2E ID
- [ ] QR Code escaneado via câmera nativa com parsing no shared
