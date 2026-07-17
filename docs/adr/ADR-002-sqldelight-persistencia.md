# ADR-002: SQLDelight como Solução de Persistência Local Compartilhada

**Status:** Aceito  
**Data:** 2026-07-17  
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

O aplicativo bancário requer persistência local para:

- Cache de dados de conta e saldo (reduzir latência e consumo de dados)
- Histórico de transações paginado
- Dados do usuário autenticado (sessão)
- Dados offline-first para extrato (regulatório: usuário deve conseguir ver últimas transações mesmo sem internet)
- Fila de operações pendentes (ex: PIX agendado, timeout de rede)

A solução de persistência precisa funcionar no módulo `shared` (KMP), portanto deve ser compatível com Android e iOS.

---

## Decisão

Adotar **SQLDelight** como solução de persistência local no módulo `shared`.

---

## Alternativas Consideradas

### Alternativa A: Room (Android only)

Room é a solução padrão Android para SQLite, mas **não é compatível com KMP**. Usar Room significaria duplicar toda a camada de persistência — esquemas SQL, DAOs e lógica de cache — em Kotlin (Room) para Android e em Swift (Core Data ou GRDB) para iOS.

**Descartada:** viola o princípio central da arquitetura de não duplicar lógica de dados.

### Alternativa B: Core Data (iOS) + Room (Android)

Manter soluções específicas por plataforma.

**Descartada:** exatamente o problema atual que queremos resolver — dois repositórios de dados, dois esquemas, dois times mantendo lógicas equivalentes.

### Alternativa C: Realm (KMP)

Realm tem suporte experimental a KMP, mas a versão KMP ainda é instável e o MongoDB Atlas Realm exige serviços externos que adicionam complexidade desnecessária para a camada local.

**Descartada:** maturidade insuficiente para contexto bancário.

### Alternativa D: SQLDelight (ESCOLHIDA)

SQLDelight gera código Kotlin type-safe a partir de esquemas SQL puros (`.sq` files). O mesmo esquema gera drivers compatíveis com Android (SQLite nativo) e iOS (SQLite via SQLiter).

---

## Por que SQLDelight é ideal para contexto bancário

| Critério | SQLDelight | Alternativas |
|---|---|---|
| Compatibilidade KMP | Nativa | Parcial ou inexistente |
| Type-safety | SQL compilado — erros em tempo de build | Room também, mas só Android |
| Schema versionado | Migrations `.sqm` versionadas no código | Sim em todas |
| Performance | SQLite nativo via driver por plataforma | Equivalente |
| Auditoria/Compliance | SQL puro legível — auditável por DBAs externos | ORM pode obscurecer queries |
| Queries complexas | SQL puro — sem limitações de ORM | Room tem limitações em joins complexos |
| Encrypt (SQLCipher) | Suportado via driver | Suportado no Room também |

### Encriptação dos dados locais (requisito bancário)

SQLDelight é compatível com **SQLCipher** — banco de dados SQLite com encriptação AES-256. A chave de encriptação é derivada de credenciais do usuário + Keystore/Secure Enclave via `expect/actual`.

```sql
-- shared/commonMain/sqldelight/database/Account.sq
CREATE TABLE Account (
    id TEXT NOT NULL PRIMARY KEY,
    holder_name TEXT NOT NULL,
    masked_number TEXT NOT NULL,
    balance_cents INTEGER NOT NULL,
    last_updated_at INTEGER NOT NULL
);

getAccountById:
SELECT * FROM Account WHERE id = ?;

upsertAccount:
INSERT OR REPLACE INTO Account VALUES (?, ?, ?, ?, ?);
```

---

## Consequências

### Positivas
- Esquema SQL único compartilhado entre Android e iOS
- Queries type-safe com erros em tempo de compilação
- Migrations versionadas — rastreável em git, auditável
- Compatível com SQLCipher para encriptação dos dados em repouso

### Negativas / Trade-offs aceitos
- Desenvolvedores precisam escrever SQL explícito (sem abstração de ORM)
- Para iOS devs, paradigma diferente de Core Data — curva de aprendizado pequena para leitura

### Dependências introduzidas

```kotlin
// libs.versions.toml
sqldelight = "2.0.2"

[libraries]
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }
```
