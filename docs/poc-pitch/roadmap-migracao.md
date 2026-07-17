# Roadmap de Migração: Apps Nativos → KMP + UI Nativa

*Proposta técnica de fases de migração — Time de Desenvolvimento Mobile*

---

## Premissas

- A migração é **incremental** — os apps atuais continuam em produção durante todo o processo
- Nenhuma feature é perdida — a paridade é mantida a cada fase
- O risco regulatório é minimizado — nenhuma mudança arquitetural em produção sem validação
- O time continua produtivo — Android devs e iOS devs trabalham em paralelo desde o início

---

## Fase 0: Fundação KMP (Meses 1–2)

**Objetivo:** Criar o módulo `shared` e validar a integração com ambos os apps existentes.

### Entregas
- [ ] Configuração do projeto KMP (Gradle, Xcode framework export)
- [ ] Pipeline de CI/CD compilando e testando shared para Android e iOS
- [ ] Primeiro módulo migrado: **validadores de domínio** (CPF, CNPJ, chave PIX, senha)
- [ ] Testes unitários do shared rodando em ambas as plataformas
- [ ] Integração do framework gerado nos apps existentes (sem mudar UI)

### Critério de sucesso
- Os apps atuais continuam funcionando identicamente
- Os validadores de CPF/CNPJ do Android app e iOS app são substituídos pelos do shared
- Zero regression em produção

---

## Fase 1: Camada de Dados Compartilhada (Meses 2–4)

**Objetivo:** Migrar a comunicação com APIs para Ktor no shared.

### Entregas
- [ ] Ktor configurado no shared com certificate pinning e autenticação OAuth
- [ ] DTOs e mappers dos endpoints principais (conta, saldo, extrato)
- [ ] Repositórios de dados no shared com estratégia offline-first (SQLDelight)
- [ ] Repositório de Autenticação (login, refresh token, logout)
- [ ] Android e iOS consumindo os repositórios do shared (ainda com ViewModels nativos)

### Critério de sucesso
- Redução de ~40% de código duplicado entre os dois projetos atuais
- Mesmas regras de cache/timeout/retry em ambas as plataformas
- Testes de repositório rodando em CI sem dispositivo físico

---

## Fase 2: Camada de Domínio e Use Cases (Meses 4–6)

**Objetivo:** Migrar toda a lógica de negócio para o shared.

### Entregas
- [ ] Use Cases de Autenticação (login, biometria, logout)
- [ ] Use Cases de Conta (saldo, extrato, paginação)
- [ ] Use Cases de PIX (validação de chave, transferência, QR Code)
- [ ] Use Cases de White-label (configuração de tema, feature flags)
- [ ] Módulo de segurança: biometria, secure storage, device integrity (expect/actual)
- [ ] Suite de testes de domínio: >80% de cobertura no shared

### Critério de sucesso
- ~70% do código de negócio nos apps está no shared
- Qualquer bug de regra de negócio tem um único ponto de correção
- Cobertura de testes do shared superior aos apps atuais combinados

---

## Fase 3: Novo App (UI Nativa sobre shared) — POC → Produção (Meses 6–12)

**Objetivo:** Construir o novo app do zero com UI nativa consumindo o shared maduro.

### Entregas por Sprint
- Sprint 1: Estrutura base Android (Compose + Navigation + Koin) e iOS (SwiftUI + NavigationStack)
- Sprint 2: Fluxo de Login/Biometria (SPEC-001)
- Sprint 3: Home — Saldo e Extrato (SPEC-002)
- Sprint 4: PIX — Transferência completa (SPEC-003)
- Sprint 5: White-label — 3 configurações de marca (SPEC-004)
- Sprint 6: Cartão de crédito, limite, fatura
- Sprint 7: Open Finance — consentimento e compartilhamento
- Sprint 8: Onboarding — abertura de conta digital
- Sprint 9–12: Features restantes, performance, acessibilidade, testes E2E

### Critério de sucesso
- App aprovado em auditoria de segurança (PCI-DSS, Banco Central)
- Performance igual ou superior ao app atual (métricas: tempo de startup, FPS, latência de biometria)
- Rating nas stores mantido ou melhorado
- Zero bugs de regressão nas features migradas

---

## Fase 4: White-label como Produto (Meses 12–18)

**Objetivo:** Transformar o app em produto white-label oferecível a parceiros.

### Entregas
- [ ] Pipeline de build por marca (CI/CD parametrizado)
- [ ] Sistema de configuração remota de tema (CDN + fallback bundled)
- [ ] SDK de integração para parceiros (documentação, onboarding)
- [ ] Painel administrativo de feature flags por marca
- [ ] Processo de certificação de novo cliente (template de contrato técnico)

---

## Comparativo de Risco por Rota

| Fase | KMP + UI Nativa | React Native |
|---|---|---|
| Mês 1–6 | Apps em produção intactos, shared sendo construído em paralelo | Rewrite completo — apps atuais congelados ou duplicados |
| Mês 6–12 | Novo app com 70% do código validado (shared) | Primeiro app completo em produção, alto risco |
| Após 12 meses | App em produção, white-label viável, time produtivo | Possível atingir paridade, time ainda em curva de aprendizado |
| Regulatório | Auditável em cada fase | Auditoria completa apenas ao final |

---

## Recursos necessários

| Recurso | Quantidade | Observação |
|---|---|---|
| Android dev sênior | 2–3 | Já no time — lidera o shared KMP |
| iOS dev sênior | 2–3 | Já no time — consome o shared em Swift |
| QA Mobile | 1–2 | Testes em dispositivos físicos Android e iOS |
| DevOps/CI | 0.5 | Configuração do pipeline KMP (Gradle + Xcode) |
| Tech Lead | 1 | Coordenação entre as plataformas e revisão de contratos |

**Diferença vs React Native:** com RN, seria necessário contratar 4–6 especialistas RN ou requalificar todo o time em 6–12 meses antes de ter qualquer entrega de valor.

---

## Marco de decisão sugerido

Após a **Fase 0** (2 meses), o time terá dados concretos:
- Percentual real de código compartilhado
- Métricas de performance do shared
- Velocidade de integração dos devs iOS com o KMP

Com esses dados em mãos, a decisão de continuar a migração tem respaldo técnico mensurável — não é mais uma discussão de arquitetura no papel.
