# SPEC-002: Saldo e Extrato de Conta

**Status:** Aprovado  
**Versão:** 1.0  
**Data:** 2026-07-17  
**Feature owner:** Time Mobile  
**Figma:** *(a ser vinculado após criação no Figma Make)*

---

## Objetivo

Implementar a tela principal (Home) com exibição de saldo e extrato de transações, demonstrando a capacidade de **offline-first** via SQLDelight, **paginação** e **estado reativo** compartilhados entre Android e iOS.

---

## Escopo da POC

Esta feature demonstra:
- ✅ Repositório com estratégia offline-first (cache SQLDelight → API Ktor)
- ✅ Paginação de extrato via `Flow<PagingData<Transaction>>` compartilhado
- ✅ Formatação de valores monetários no shared (sem depender de locale nativo)
- ✅ Ocultação de saldo (toggle "esconder/mostrar") com estado no shared
- ✅ Pull-to-refresh nativo consumindo use case compartilhado

---

## Fluxo de dados (offline-first)

```
[Abrir tela Home]
        │
        ├─→ [Emitir dados do cache (SQLDelight)] ──→ [Exibir na UI imediatamente]
        │
        └─→ [Buscar dados frescos da API (Ktor)]
                    │
                    ├─[Sucesso]──→ [Atualizar cache] ──→ [Flow emite novo valor] ──→ [UI atualiza]
                    └─[Erro de rede]──→ [Manter cache exibido + banner "dados desatualizados"]
```

---

## Contratos do shared (KMP)

### Estado de UI

```kotlin
// shared/commonMain/feature/home/HomeUiState.kt
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val account: AccountDisplay? = null,
    val isBalanceHidden: Boolean = false,
    val transactions: List<TransactionDisplay> = emptyList(),
    val hasMoreTransactions: Boolean = true,
    val isLoadingMoreTransactions: Boolean = false,
    val dataStatus: DataStatus = DataStatus.Fresh,
    val error: HomeError? = null
)

data class AccountDisplay(
    val holderName: String,
    val maskedNumber: String,        // "•••• 4521"
    val balanceFormatted: String,    // "R$ 1.234,56" ou "R$ ••••••" (oculto)
    val availableLimitFormatted: String
)

data class TransactionDisplay(
    val id: String,
    val description: String,
    val amountFormatted: String,     // "- R$ 50,00" ou "+ R$ 200,00"
    val isDebit: Boolean,
    val dateFormatted: String,       // "Hoje", "Ontem", "15 jul."
    val category: TransactionCategory
)

sealed class DataStatus {
    object Fresh : DataStatus()
    data class Stale(val lastUpdatedAt: Long) : DataStatus()
}

enum class TransactionCategory {
    PIX, TED, BOLETO, CARD, PURCHASE, DEPOSIT, FEE, OTHER
}
```

### Use Cases

```kotlin
// 1. Observar dados da conta (Flow — reativo)
class ObserveAccountUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(accountId: String): Flow<Result<Account>>
}

// 2. Carregar extrato com paginação
class GetTransactionHistoryUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        accountId: String,
        page: Int,
        pageSize: Int = 20
    ): Result<TransactionPage>
}

// 3. Forçar refresh dos dados
class RefreshAccountDataUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(accountId: String): Result<Unit>
}

// 4. Toggle de ocultação de saldo
class ToggleBalanceVisibilityUseCase(private val preferencesRepository: PreferencesRepository) {
    suspend operator fun invoke()
    fun observe(): Flow<Boolean> // isHidden
}
```

### Repositórios (interfaces)

```kotlin
interface AccountRepository {
    fun observeAccount(accountId: String): Flow<Account>
    suspend fun refreshFromRemote(accountId: String): Result<Unit>
}

interface TransactionRepository {
    suspend fun getTransactions(accountId: String, page: Int, pageSize: Int): Result<TransactionPage>
    fun observeRecentTransactions(accountId: String, limit: Int = 5): Flow<List<Transaction>>
}
```

---

## Regras de negócio (no shared)

| Regra | Detalhe |
|---|---|
| Formatação de valores | `R$ 1.234,56` — separador de milhar ponto, decimal vírgula |
| Valores negativos | Exibidos em vermelho com prefixo `- R$` |
| Valores positivos | Exibidos em verde com prefixo `+ R$` |
| Saldo oculto | Exibir `R$ ••••••` — preferência salva no `SecureStorage` |
| Agrupamento de data | Transações agrupadas por dia: "Hoje", "Ontem", data formatada |
| Cache stale | Dados com mais de 5 minutos mostram banner "Última atualização: HH:mm" |
| Scroll infinito | Carregar próxima página ao chegar em 80% do scroll |

---

## Requisitos de UI

### Tela Home

| Elemento | Detalhe |
|---|---|
| Header com nome e avatar | Nome do titular, iniciais como avatar |
| Card de saldo | Número da conta mascarado, saldo, botão olho (ocultar/mostrar) |
| Banner de dados stale | Condicional — amarelo, discreto |
| Atalhos rápidos | PIX, Transferir, Pagar, Recarga (botões de ação rápida) |
| Lista de transações | Agrupada por data, scroll infinito, ícones por categoria |
| Pull-to-refresh | Nativo em ambas as plataformas |
| Loading skeleton | Skeleton enquanto dados carregam (não spinner genérico) |

---

## Testes requeridos (shared)

```kotlin
class ObserveAccountUseCaseTest {
    @Test fun `deve emitir dados do cache imediatamente`()
    @Test fun `deve emitir dados atualizados após refresh da API`()
    @Test fun `deve marcar dados como stale se mais de 5 minutos`()
}

class GetTransactionHistoryUseCaseTest {
    @Test fun `deve retornar primeira página corretamente`()
    @Test fun `deve retornar hasMore=false na última página`()
    @Test fun `deve agrupar transações por data`()
}

class CurrencyFormatterTest {
    @Test fun `deve formatar 123456 centavos como R$ 1234,56`()
    @Test fun `deve formatar valor negativo com prefixo menos`()
    @Test fun `deve retornar bullets quando saldo oculto`()
}
```

---

## Critérios de aceite da POC

- [ ] Saldo exibido imediatamente ao abrir (dados do cache)
- [ ] Pull-to-refresh funciona em Android e iOS consumindo o mesmo use case
- [ ] Toggle de saldo persiste entre sessões (salvo no SecureStorage compartilhado)
- [ ] Scroll infinito carrega próxima página ao chegar em 80%
- [ ] Banner "dados desatualizados" aparece quando sem conexão
- [ ] Formatação monetária idêntica em Android e iOS (lógica no shared)
