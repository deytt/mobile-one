# Métricas reais — mobile-one POC

*Snapshot coletado em **2026-07-21**. Reproduzir com `python3 scripts/measure_code_metrics.py` e `./gradlew :shared:allTests`.*

---

## Resumo executivo

| Indicador | Valor | Leitura para gestores |
|---|---|---|
| Módulo `shared` / app total | **21%** (code) · **24%** (physical) | UI nativa (Compose + SwiftUI) é intencionalmente grande |
| `commonMain` / app total | **14%** (code) · **17%** (physical) | Código escrito **uma vez** para Android e iOS |
| `commonMain` / módulo `shared` | **68%** (code) · **70%** (physical) | Maioria do shared é 100% comum; o resto é `expect/actual` |
| Lógica de negócio (domain + data + config) | **100%** em `commonMain` | Sem cópia de regras entre plataformas |
| Use cases + validadores | **100%** compartilhados | Meta ≥ 95% — **atingida** |
| Testes no shared | **88** · **0 falhas** · Android JVM + iOS Simulator | Mesma suíte valida as duas plataformas |
| Use cases com teste dedicado | **9 / 14 (64%)** | Lacunas listadas abaixo |

> O pitch inicial falava em **70–80% de código compartilhado**. Esse número refere-se à camada **compartilhável** (dados + domínio), não ao app inteiro com UI. Com as telas nativas implementadas, o % do **repositório todo** cai — e isso é coerente com a tese do ADR-001: compartilhar o que não pode divergir, manter UI nativa.

---

## 1. Compartilhamento de código

### Metodologia

- Extensões: `.kt`, `.swift`, `.sq`
- **code**: linhas não vazias, sem comentários `//` e `/* */`
- **physical**: todas as linhas do arquivo (histórico do sprint-log)
- Fórmula do módulo shared (como no sprint-log):

```
% shared = (commonMain + androidMain + iosMain) / (shared + androidApp + iosApp) × 100
```

`commonTest` **não** entra no denominador de produção.

### LOC por source set

| Source set | Arquivos | LOC (code) | LOC (physical) |
|---|---:|---:|---:|
| `shared/commonMain` | 64 | 1 514 | 2 135 |
| `shared/androidMain` | 9 | 184 | 258 |
| `shared/iosMain` | 9 | 522 | 670 |
| **Módulo shared** | **82** | **2 220** | **3 063** |
| `androidApp` (Compose) | 40 | 5 179 | 5 704 |
| `iosApp` (SwiftUI) | 36 | 3 366 | 3 855 |
| **Total produção** | **158** | **10 765** | **12 622** |
| `shared/commonTest` (à parte) | 20 | 799 | 1 043 |

### Percentuais

| Métrica | Code | Physical |
|---|---:|---:|
| Módulo `shared` / total | **20,6%** | **24,3%** |
| Só `commonMain` / total | **14,1%** | **16,9%** |
| `commonMain` / módulo `shared` | **68,2%** | **69,7%** |

### Por camada (o que importa para o banco)

| Camada | Onde vive | Compartilhado? |
|---|---|---|
| Use cases + validadores (CPF, PIX, auth) | `commonMain` | **100%** |
| Repositórios / fakes / SQLDelight schema | `commonMain` | **100%** |
| White-label (`BrandCatalog`, tokens) | `commonMain` | **100%** |
| Biometria, Keystore, QR, HTTP engine | `expect` + `androidMain` / `iosMain` | Contrato comum, impl nativa |
| Telas e navegação | `androidApp` / `iosApp` | **0%** (nativo de propósito) |

### Economia de duplicação

Se a lógica de `commonMain` tivesse de ser reescrita em cada app:

| Cenário | LOC (physical) |
|---|---:|
| Sem KMP (`commonMain` × 2 + adapters + UIs) | 14 757 |
| Com KMP (atual) | 12 622 |
| **Linhas evitadas** | **2 135 (~14,5%)** |

O ganho real não é só “menos linhas”: é **uma única fonte de verdade** para regras regulatórias (PIX, CPF, limites) nas duas plataformas.

---

## 2. Cobertura de testes

### Resultado da suíte (`./gradlew :shared:allTests`)

| Plataforma | Testes | Falhas | Erros |
|---|---:|---:|---:|
| Android (`testDebugUnitTest`) | 88 | 0 | 0 |
| iOS Simulator (`iosSimulatorArm64Test`) | 88 | 0 | 0 |

19 classes de teste em `commonTest`. Os mesmos 88 casos rodam nas duas plataformas.

### Cobertura por inventário (arquivo ↔ `*Test.kt`)

Kover ainda não está no Gradle; a cobertura abaixo é por **inventário de testes dedicados** sobre `commonMain` (não instrumentação de linhas).

| Pacote | Arquivos | LOC code | Com teste relacionado | Notas |
|---|---:|---:|---:|---|
| `domain/usecase` | 14 | 339 | **9/14 (64%)** · ~77% LOC | Ver lacunas |
| `domain/validation` | 6 | 119 | ~92% LOC | Via `AuthValidatorTest` + `ValidatePixKeyUseCaseTest` |
| `config` (white-label) | 4 | 212 | **100%** | Meta da SPEC-004 |
| `util` | 1 | 58 | **100%** | `CurrencyFormatterTest` |
| `data` | 8 | 322 | ~40% LOC | Fakes de PIX/conta/transação sem teste próprio |
| `feature` (UiState) | 4 | 136 | 0% | DTOs de apresentação — baixo risco |
| `security` (expect) | 4 | 39 | 0% | Contratos; cobertura via use cases / actuals |

### Use cases

| Use case | Teste dedicado |
|---|---|
| `LoginWithCredentialsUseCase` | ✅ |
| `LoginWithBiometricUseCase` | ✅ |
| `SetupBiometricLoginUseCase` | ✅ |
| `ValidateDeviceIntegrityUseCase` | ✅ |
| `ObserveAccountUseCase` | ✅ |
| `GetTransactionHistoryUseCase` | ✅ |
| `ValidatePixKeyUseCase` | ✅ |
| `ParsePixQRCodeUseCase` | ✅ |
| `SwitchBrandUseCase` | ✅ |
| `DetectPixKeyTypeUseCase` | ❌ |
| `ExecutePixTransferUseCase` | ❌ |
| `LookupPixRecipientUseCase` | ❌ |
| `RefreshAccountDataUseCase` | ❌ |
| `ToggleBalanceVisibilityUseCase` | ❌ |

### Versus metas da POC

| Meta (`metricas-poc.md`) | Alvo | Status |
|---|---|---|
| Use cases + validadores compartilhados | ≥ 95% | ✅ **100%** |
| Camada de dados compartilhada | ≥ 90% | ✅ **100%** em `commonMain` |
| Total do projeto compartilhado | ≥ 65% | ❌ **~21%** — meta original misturava UI; ver nota abaixo |
| Cobertura use cases | ≥ 80% | ⚠️ **64%** arquivos / ~77% LOC com teste |
| Cobertura validadores | 100% | ⚠️ Alta via suítes agregadas; falta teste isolado de `RandomKeyValidator` |
| Total shared (linha instrumentada) | ≥ 75% | ⏳ Pendente Kover |

**Nota sobre a meta de 65% do app total:** com UI nativa completa (6 fluxos × 3 marcas), o denominador cresce quase só em Compose/SwiftUI. A meta útil para a tese KMP é: **≥ 95% da lógica de negócio compartilhada** — já cumprida. Recomenda-se recalibrar a meta de “% do repositório” para **≥ 20% shared module** neste estágio da POC, ou medir só `domain`+`data`+`config`.

---

## 3. Como repetir a medição

```bash
# LOC + inventário
python3 scripts/measure_code_metrics.py

# Testes nas duas plataformas
./gradlew :shared:allTests
```

Próximo passo sugerido: adicionar o plugin **Kover** em `:shared` para cobertura de linhas instrumentada (meta ≥ 75% no shared).

---

## 4. Dashboard (números reais)

```
┌─────────────────────────────────────────────────────────────────┐
│                 mobile-one POC — 2026-07-21                     │
├───────────────────┬─────────────────────────────────────────────┤
│ Shared / app      │ ████░░░░░░░░░░░░░░░░  21% (code)            │
│ commonMain/shared │ ██████████████░░░░░░  68%                   │
│ Negócio em common │ ████████████████████ 100%                   │
│ Testes shared     │ 88 ✅  Android + iOS Simulator              │
│ Use cases c/ teste│ ████████████░░░░░░░░  9/14 (64%)            │
│ Features UI       │ Splash · Login · Bio · Home · Cartões ·     │
│                   │ Brand Switcher · PIX (Android + iOS)        │
└───────────────────┴─────────────────────────────────────────────┘
```
