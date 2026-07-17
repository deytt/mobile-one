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

## Sprint 1 — Setup KMP + Fundação Shared

*Em andamento — executado em agente dedicado*

### Planejado
- [ ] Configuração do projeto KMP (Gradle multiplatform — `shared`, `androidApp`, `iosApp`)
- [ ] `libs.versions.toml` com Ktor, SQLDelight, Koin, Coroutines
- [ ] `WhiteLabelConfig` com as 3 marcas (tokens extraídos do Figma)
- [ ] JSON bundled das 3 marcas em `shared/commonMain/resources/white_label/`
- [ ] Entidades de domínio: `Account`, `Transaction`, `AuthToken`, `PixTransfer`
- [ ] Interfaces de repositório: `AuthRepository`, `AccountRepository`, `PixRepository`
- [ ] Fake repositories para todas as features
- [ ] `LoginWithCredentialsUseCase` + `LoginWithBiometricUseCase` + testes
- [ ] `ObserveAccountUseCase` + `GetTransactionHistoryUseCase` + testes
- [ ] `ValidatePixKeyUseCase` + validadores (CPF, CNPJ, email, UUID) + testes
- [ ] `BiometricAuthenticator` (expect/actual)
- [ ] `SecureStorage` (expect/actual)
- [ ] Koin modules: `domainModule`, `fakeDataModule`

### Referências para implementação
- Design System: [`docs/figma/design-system.md`](../figma/design-system.md)
- Spec Auth: [`docs/specs/SPEC-001-login-biometrico.md`](../specs/SPEC-001-login-biometrico.md)
- Spec Conta: [`docs/specs/SPEC-002-saldo-extrato.md`](../specs/SPEC-002-saldo-extrato.md)
- Spec PIX: [`docs/specs/SPEC-003-pix.md`](../specs/SPEC-003-pix.md)
- Spec White-label: [`docs/specs/SPEC-004-white-label-config.md`](../specs/SPEC-004-white-label-config.md)
- ADR KMP vs RN: [`docs/adr/ADR-001-kmp-vs-react-native.md`](../adr/ADR-001-kmp-vs-react-native.md)

### Métricas (a preencher ao final)

| Métrica | Valor |
|---|---|
| Linhas no shared | — |
| Linhas no androidApp | — |
| Linhas no iosApp | — |
| % compartilhado | — |
| Cobertura de testes shared | — |
| Módulos KMP configurados | — |

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
