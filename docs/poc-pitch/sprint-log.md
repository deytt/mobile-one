# Sprint Log — mobile-one POC

*Registro de progresso, métricas e observações por sprint.*

---

## Sprint 0 — Estrutura, Documentação e Design (2026-07-17) ✅ CONCLUÍDO

### Entregas
- [x] Estrutura de documentação criada (`docs/adr/`, `docs/specs/`, `docs/poc-pitch/`, `docs/figma/`)
- [x] 5 ADRs escritos (argumentos técnicos para gestores)
- [x] 4 Specs de features aprovadas e vinculadas ao Figma
- [x] Material de pitch para gestores
- [x] Memory Bank (`.cursor/rules/`) configurado para contexto de agente
- [x] README atualizado com estrutura completa
- [x] **Design System Figma criado (v2.0) — 7 telas, 3 marcas**
- [x] **Tokens de cor documentados e mapeados para `WhiteLabelConfig`**
- [x] `docs/figma/design-system.md` — mapa completo de nós e tokens

### Design entregue

| Tela | node-id | Status |
|---|---|---|
| Splash | `28:19512` | ✅ |
| Login | `29:20015` | ✅ |
| Biometria | `29:20689` | ✅ |
| Home / Conta | `29:21243` | ✅ |
| Cartões | `29:22301` | ✅ |
| Brand Switcher | `29:23293` | ✅ |
| Fluxo PIX | `15:9848` | ✅ |

### Métricas finais

| Métrica | Valor |
|---|---|
| Linhas no shared | 0 (não iniciado) |
| Linhas no androidApp | 0 (não iniciado) |
| Linhas no iosApp | 0 (não iniciado) |
| Specs aprovadas | 4/4 |
| ADRs escritos | 5/5 |
| Telas no Figma | 7/7 |
| Marcas no design system | 3/3 |
| Features implementadas | 0/4 |

---

## Sprint 1 — Setup KMP + Fundação Shared (scaffolding) ✅ CONCLUÍDO (parte 1/2)

*Escopo executado: fundação/scaffolding do projeto KMP. O domínio de negócio (entidades, use
cases, validadores, fake repositories, WhiteLabelConfig) fica para PRs seguintes, um por spec
(SPEC-001 a SPEC-004), conforme o workflow "spec-driven" e a regra de PRs pequenos e focados.*

### Entregue (scaffolding)
- [x] Configuração do projeto KMP (Gradle multiplatform — `shared`, `androidApp`, `iosApp`)
- [x] `libs.versions.toml` com Kotlin 2.3.21, AGP 8.13.2, Ktor 3.5.1, SQLDelight 2.3.2, Koin 4.2.2, Coroutines 1.11.0
- [x] `shared`: targets Android + iOS (arm64/simulatorArm64/x64), framework nativo exportado
- [x] Placeholders `expect/actual`: `Platform`, `DatabaseDriverFactory` (SQLDelight), `createHttpClientEngine` (Ktor) — validam o padrão do ADR-005 antes de qualquer implementação real
- [x] `initKoin` compartilhado (módulo vazio por ora)
- [x] Estrutura de pacotes `domain/{entity,usecase,repository,error}` e `data/{remote,local,repository}` (vazias, com README de convenção)
- [x] Testes smoke em `commonTest` rodando via `./gradlew :shared:allTests` (Android + iOS Simulator)
- [x] `androidApp` (Jetpack Compose) consumindo o `shared` — tela "Hello, mobile-one!" com `Platform().name`
- [x] `iosApp` (SwiftUI, projeto gerado via XcodeGen) consumindo o `shared` — mesma tela, build e execução validados no simulador iOS
- [x] `.github/workflows/ci.yml` com jobs `android` (ubuntu-latest) e `ios` (macos-latest)

### Planejado para PRs seguintes (por spec)
- [ ] `WhiteLabelConfig` com as 3 marcas (tokens extraídos do Figma) — SPEC-004
- [ ] JSON bundled das 3 marcas em `shared/commonMain/resources/white_label/` — SPEC-004
- [ ] Entidades de domínio: `Account`, `Transaction`, `AuthToken`, `PixTransfer`
- [ ] Interfaces de repositório: `AuthRepository`, `AccountRepository`, `PixRepository`
- [ ] Fake repositories para todas as features
- [ ] `LoginWithCredentialsUseCase` + `LoginWithBiometricUseCase` + testes — SPEC-001
- [ ] `ObserveAccountUseCase` + `GetTransactionHistoryUseCase` + testes — SPEC-002
- [ ] `ValidatePixKeyUseCase` + validadores (CPF, CNPJ, email, UUID) + testes — SPEC-003
- [ ] `BiometricAuthenticator` (expect/actual real) — ADR-005
- [ ] `SecureStorage` (expect/actual real) — ADR-005
- [ ] Koin modules: `domainModule`, `fakeDataModule`

### Referências para implementação
- Design System: [`docs/figma/design-system.md`](../figma/design-system.md)
- Spec Auth: [`docs/specs/SPEC-001-login-biometrico.md`](../specs/SPEC-001-login-biometrico.md)
- Spec Conta: [`docs/specs/SPEC-002-saldo-extrato.md`](../specs/SPEC-002-saldo-extrato.md)
- Spec PIX: [`docs/specs/SPEC-003-pix.md`](../specs/SPEC-003-pix.md)
- Spec White-label: [`docs/specs/SPEC-004-white-label-config.md`](../specs/SPEC-004-white-label-config.md)
- ADR KMP vs RN: [`docs/adr/ADR-001-kmp-vs-react-native.md`](../adr/ADR-001-kmp-vs-react-native.md)

### Métricas (scaffolding — sem domínio de negócio ainda)

| Métrica | Valor |
|---|---|
| Linhas no shared (`.kt` + `.sq`) | 172 (14 arquivos) |
| Linhas no androidApp (`.kt`) | 82 (3 arquivos) |
| Linhas no iosApp (`.swift`) | 33 (2 arquivos) |
| % compartilhado (shared / total de código) | ~60% (172 / 287) — esperado subir para 70–80% conforme entidades/use cases entrarem por spec |
| Cobertura de testes shared | 3 testes smoke (Platform, HttpClient, Koin), rodando em Android + iOS Simulator |
| Módulos KMP configurados | `shared` (Android + iosArm64/iosSimulatorArm64/iosX64), `androidApp`, `iosApp` |

---

## Sprint 2 — UI Android (Compose) — planejado

- [ ] `BankTheme` consumindo `WhiteLabelConfig` (mapeamento token → MaterialTheme)
- [ ] Tela Splash (SPEC-001 / node `28:19512`)
- [ ] Tela Login (SPEC-001 / node `29:20015`)
- [ ] Tela Biometria (SPEC-001 / node `29:20689`)
- [ ] Tela Home/Conta (SPEC-002 / node `29:21243`)
- [ ] Tela Cartões (SPEC-002 / node `29:22301`)
- [ ] Tela Brand Switcher (SPEC-004 / node `29:23293`)
- [ ] Navegação completa (Splash → Login/Bio → Home → Cartões → Brand Switcher)

---

## Sprint 3 — UI iOS (SwiftUI) — planejado

- [ ] `BankThemeEnvironment` consumindo `WhiteLabelConfig`
- [ ] Mesmas 6 telas em SwiftUI (mesmos nodes Figma)
- [ ] Validar paridade visual Android ↔ iOS
- [ ] Validar que troca de marca funciona igual nas duas plataformas

---

## Sprint 4 — Fluxo PIX + Métricas finais — planejado

- [ ] Fluxo PIX Android (8 telas, node `15:9848`)
- [ ] Fluxo PIX iOS
- [ ] Coletar métricas de % código compartilhado
- [ ] Preparar demo para gestores
- [ ] Atualizar `docs/poc-pitch/metricas-poc.md` com dados reais
