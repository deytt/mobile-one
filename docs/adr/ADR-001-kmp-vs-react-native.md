# ADR-001: KMP com UI Nativa vs React Native

**Status:** Aceito  
**Data:** 2026-07-17  
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

O banco possui dois aplicativos nativos em produção — um Android (Kotlin/Compose) e um iOS (Swift/SwiftUI) — com as mesmas regras de negócio implementadas em duplicidade. A gestão decidiu unificá-los em uma única codebase React Native, com os objetivos de:

1. Reduzir duplicidade de código
2. Tornar o app white-label
3. Ter um único time mobile

O time técnico avaliou a decisão e identificou riscos significativos nessa abordagem para um aplicativo bancário de grande porte, regulado pelo Banco Central do Brasil.

---

## Decisão

Adotar **Kotlin Multiplatform (KMP)** para compartilhamento das camadas de **Dados e Domínio**, mantendo **UI 100% nativa** com Jetpack Compose (Android) e SwiftUI (iOS).

---

## Alternativas Consideradas

### Alternativa A: React Native (proposta da gestão)

**Vantagens:**
- Uma única codebase de UI
- Ecossistema JavaScript amplo
- Reconhecimento de mercado

**Desvantagens críticas para contexto bancário:**

| Risco | Impacto | Detalhe |
|---|---|---|
| Performance | Alto | Bridge JavaScript introduz latência em operações críticas (biometria, câmera para foto de documento, animações de segurança) |
| Segurança | Alto | Lógica de negócio em JavaScript é mais fácil de reverse-engineer; proteção de código mais fraca que bytecode Kotlin/Swift compilado |
| APIs nativas | Alto | Acesso a Android Keystore e iOS Secure Enclave via bridge — camada extra de falha em operações críticas de criptografia |
| Atualização de SO | Médio | Cada major release de Android/iOS historicamente quebra funcionalidades via bridge (notórios nos ciclos iOS 14, 15, 16) |
| Know-how | Alto | O banco perderia expertise nativa acumulada ao longo de anos; precisaria contratar/requalificar toda a equipe em RN |
| Regulatório | Médio | Certificações de segurança (PCI-DSS, Banco Central) auditam código nativo — bridge JavaScript gera questionamentos adicionais |
| Metro Bundler | Baixo/Médio | Tamanho do bundle, carregamento e hot reload em produção afetam a percepção de qualidade |

**Percentual de código compartilhado:** ~85% (UI + lógica juntos, mas na linguagem errada para performance bancária)

### Alternativa B: Compose Multiplatform (iOS + Android)

**Vantagens:**
- UI compartilhada em Kotlin
- Um único time de UI

**Desvantagens:**
- Compose Multiplatform para iOS ainda em Alpha/Beta para features complexas
- Não usa componentes nativos do iOS — sensação de "não-nativo" para usuários iOS
- Suporte a recursos iOS específicos (Face ID, Dynamic Island, widgets) limitado
- Risco alto para um app de produção bancário em 2026

**Decisão:** Descartada para esta fase. A considerar quando o Compose Multiplatform para iOS atingir GA estável.

### Alternativa C: KMP com UI Nativa (ESCOLHIDA)

**Vantagens:**

| Critério | Resultado |
|---|---|
| Código compartilhado | 70–80% (dados + domínio) |
| Performance | 100% nativa em ambas as plataformas |
| Segurança | Acesso direto a Keystore/Secure Enclave sem bridge |
| Know-how | Time Android e iOS continuam nas suas especialidades |
| Regulatório | Código compilado nativo — sem questionamentos adicionais |
| White-label | Suportado via configuração no shared (tokens de tema, feature flags) |
| Testabilidade | Use cases testados uma vez, rodando em Android e iOS |
| Maturidade | KMP é GA (Kotlin 2.0) — usado por Netflix, Cash App, 1Password em produção |

---

## Consequências

### Positivas
- Time Android e iOS mantêm expertise e produtividade desde o dia 1
- Regras de negócio escritas e testadas uma única vez
- Bugs de lógica corrigidos em uma única base de código
- Performance e experiência de usuário indistinguível de um app 100% nativo
- Caminho natural para adotar Compose Multiplatform no futuro quando maduro

### Negativas / Trade-offs aceitos
- iOS devs precisam ler (não necessariamente escrever) Kotlin para entender os contratos do shared
- Configuração inicial do projeto KMP tem overhead maior que um projeto nativo simples
- Algumas bibliotecas Android não têm equivalente KMP — requires `expect/actual` manual em casos específicos

### Neutras
- O banco continua com dois módulos de UI (Android e iOS) — isso é intencional e desejado
- CI/CD precisa compilar e testar para ambas as plataformas (já era o caso com dois repos separados)

---

## Métricas de Sucesso da POC

- [ ] ≥ 70% do código total está no módulo `shared`
- [ ] Um único conjunto de testes unitários roda em Android e iOS
- [ ] Feature de Login com Biometria funcional em ambas as plataformas
- [ ] Feature PIX funcional demonstrando regras de Open Finance compartilhadas
- [ ] White-label: troca de tema (cores/logo) sem alterar código de UI
- [ ] Nenhuma lógica de negócio duplicada entre `androidApp` e `iosApp`
