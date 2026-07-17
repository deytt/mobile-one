# Sprint Log — mobile-one POC

*Registro de progresso, métricas e observações por sprint.*

---

## Sprint 0 — Estrutura e Documentação (2026-07-17)

### Entregas
- [x] Estrutura de documentação criada (`docs/adr/`, `docs/specs/`, `docs/poc-pitch/`)
- [x] 5 ADRs escritos (argumentos técnicos para gestores)
- [x] 4 Specs de features aprovadas
- [x] Material de pitch para gestores
- [x] Memory Bank (`.cursor/rules/`) configurado para contexto de agente
- [x] README atualizado com estrutura completa

### Próximos passos

- [ ] Criar projeto KMP no Android Studio (configuração Gradle + Xcode framework)
- [ ] Configurar CI/CD para compilar shared em Android e iOS
- [ ] Criar telas no Figma Make (Login, Home, PIX, White-label Demo)
- [ ] Iniciar implementação SPEC-001 (Login com Biometria)

### Métricas

| Métrica | Valor |
|---|---|
| Linhas no shared | 0 (não iniciado) |
| Linhas no androidApp | 0 (não iniciado) |
| Linhas no iosApp | 0 (não iniciado) |
| Specs aprovadas | 4/4 |
| ADRs escritos | 5/5 |
| Features implementadas | 0/4 |

---

## Sprint 1 — Setup KMP + SPEC-001: Login com Biometria

*A iniciar*

### Planejado
- [ ] Configuração do projeto KMP (Gradle multiplatform)
- [ ] Configuração do Ktor, SQLDelight e Koin no shared
- [ ] Módulo de segurança: `BiometricAuthenticator` (expect/actual)
- [ ] Módulo de segurança: `SecureStorage` (expect/actual)
- [ ] `AuthRepository` (interface + implementação + Ktor)
- [ ] `LoginWithCredentialsUseCase` + testes
- [ ] `LoginWithBiometricUseCase` + testes
- [ ] Telas no Figma Make: Login com Senha + Boas-vindas com Biometria
- [ ] UI Android (Compose) — SPEC-001
- [ ] UI iOS (SwiftUI) — SPEC-001

### Métricas (a preencher ao final)

| Métrica | Valor |
|---|---|
| Linhas no shared | — |
| Linhas no androidApp | — |
| Linhas no iosApp | — |
| % compartilhado | — |
| Cobertura de testes | — |
| Bugs encontrados | — |
| Bugs corrigidos no shared (fix em 1x) | — |

### Observações

*(a preencher ao final do sprint)*
