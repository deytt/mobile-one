# ADR-001: KMP com UI Nativa vs React Native

**Status:** Aceito
**Data:** 2026-07-17
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

O produto mobile precisa reduzir duplicidade de regras entre Android e iOS, preservar qualidade de experiência nativa e suportar cenários white-label. Também há requisitos relevantes de segurança, auditoria, integração com APIs nativas e evolução incremental dos aplicativos existentes.

Foram avaliadas alternativas cross-platform com foco em:

1. Compartilhamento de regras de negócio e camada de dados
2. Experiência de usuário consistente com cada sistema operacional
3. Capacidade de adoção incremental
4. Facilidade de auditoria e integração com recursos de segurança nativos
5. Sustentabilidade técnica para times Android e iOS

---

## Decisão

Adotar **Kotlin Multiplatform (KMP)** para compartilhamento das camadas de **dados, domínio, configuração e contratos de segurança**, mantendo **UI nativa** com Jetpack Compose no Android e SwiftUI no iOS.

---

## Alternativas Consideradas

### Alternativa A: React Native

**Vantagens:**

- Compartilhamento amplo de código, incluindo parte relevante da UI
- Ecossistema JavaScript/TypeScript maduro
- Boa adoção de mercado e comunidade ativa
- Produtividade elevada em cenários de interface padronizada

**Pontos de atenção para o contexto avaliado:**

| Critério | Observação |
|---|---|
| Segurança | Integrações com Keystore, Secure Enclave, biometria e armazenamento seguro dependem de módulos nativos expostos ao JavaScript |
| Auditoria | A presença de uma camada JavaScript/bridge adiciona um componente a ser analisado em fluxos sensíveis |
| Performance percebida | Fluxos com biometria, câmera, animações e inicialização podem exigir otimizações específicas por plataforma |
| Evolução de sistema operacional | Mudanças em APIs de Android e iOS podem exigir atualização de bibliotecas nativas e validação adicional da bridge |
| Conhecimento especializado | Parte do conhecimento nativo continua necessária para módulos críticos, publicação, profiling e troubleshooting |

React Native permanece tecnicamente viável para diversos cenários cross-platform. Para esta POC, os requisitos de segurança nativa, adoção incremental e preservação de UI nativa favoreceram outra decisão arquitetural.

### Alternativa B: Compose Multiplatform para Android e iOS

**Vantagens:**

- Possibilidade de compartilhar UI em Kotlin
- Modelo declarativo semelhante ao Jetpack Compose
- Alinhamento natural com KMP

**Pontos de atenção:**

- Maturidade em iOS ainda em evolução para cenários bancários complexos
- Menor aderência a componentes e padrões nativos de iOS
- Necessidade de validação adicional para recursos específicos de plataforma

**Decisão:** não adotar nesta fase. A alternativa pode ser reavaliada conforme a maturidade do ecossistema evoluir.

### Alternativa C: KMP com UI Nativa

**Vantagens:**

| Critério | Resultado esperado |
|---|---|
| Compartilhamento | Regras de negócio, dados, validadores, configuração e contratos em `shared` |
| UX | Interfaces implementadas com frameworks nativos de cada plataforma |
| Segurança | Acesso direto a APIs nativas via `expect/actual` |
| Testabilidade | Mesma suíte em `commonTest` validando domínio compartilhado |
| Adoção | Possibilidade de migração incremental por módulo ou feature |
| White-label | Tokens e feature flags centralizados no módulo compartilhado |

---

## Consequências

### Positivas

- Regras de negócio escritas e testadas uma única vez
- Menor risco de divergência funcional entre Android e iOS
- UI preserva padrões, acessibilidade e performance de cada plataforma
- Integrações sensíveis ficam próximas das APIs nativas
- A arquitetura permite evolução incremental sem exigir reescrita completa de todos os módulos de uma só vez

### Trade-offs aceitos

- O projeto mantém duas implementações de UI, uma por plataforma
- Desenvolvedores iOS precisam compreender os contratos expostos pelo módulo KMP
- A configuração inicial do build multiplatform exige disciplina de CI/CD
- APIs sem biblioteca multiplatform requerem contratos `expect/actual`

### Neutras

- Android e iOS continuam com seus toolchains nativos de build, profiling e publicação
- O módulo compartilhado passa a ser um contrato central entre as plataformas

---

## Métricas de Sucesso

- [x] Um único conjunto de testes unitários roda em Android JVM e iOS Simulator
- [x] Lógica de negócio, validadores e configuração white-label residem em `commonMain`
- [x] Fluxos de autenticação, conta, cartões, PIX e white-label consomem contratos compartilhados
- [x] Interfaces Android e iOS permanecem nativas
- [ ] Cobertura de linhas instrumentada no `shared` com Kover
- [ ] Integração com pipelines corporativos de CI/CD
