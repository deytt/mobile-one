# Comparativo Técnico: KMP com UI Nativa vs React Native

*Documento para apresentação à gestão — Time de Desenvolvimento Mobile*

---

## Resumo Executivo

| Critério | React Native | KMP + UI Nativa |
|---|---|---|
| Código compartilhado | ~85% (UI + lógica) | **70–80% (lógica pura)** |
| Performance | Limitada pela bridge JS | **100% nativa** |
| Segurança (contexto bancário) | Risco adicional (bridge) | **Equivalente ao app atual** |
| Experiência do usuário | Aproximada do nativo | **Indistinguível do nativo** |
| Know-how do time | **Perdido** (requalificação total) | **Preservado** |
| Maturidade para banco | Médio (casos complexos) | **Alto (KMP GA, Kotlin 2.0)** |
| White-label | Possível | **Possível (com vantagem arquitetural)** |
| Custo de migração | Alto (rewrite total + contratação) | **Médio (shared incremental)** |

---

## Por que "mais código compartilhado" não é necessariamente melhor

O React Native compartilha ~85% do código, incluindo a UI. Isso soa vantajoso, mas há um problema fundamental: **a UI é a camada que mais muda**.

Layouts mudam a cada sprint para seguir novos padrões de design, guidelines do sistema operacional, e feedback de UX. Ter a UI acoplada à lógica de negócio em uma única codebase JavaScript significa:

- Mudanças de design obrigam devs que não conhecem design system a mexer no mesmo arquivo que tem regras de negócio
- Um bug de renderização no Android pode quebrar a compilação do iOS e vice-versa
- Performance de animações e transições — crítico em apps bancários (loading de biometria, animação de confirmação de PIX) — depende da bridge

**O KMP compartilha a parte que NUNCA deve mudar** (regras de negócio, validadores, comunicação com API do Banco Central) e **deixa livre a parte que muda com frequência** (UI).

---

## Análise de Risco para um Banco Regulado pelo Banco Central

### Risco 1: Auditoria de Segurança

O Banco Central e auditorias PCI-DSS exigem que dados sensíveis (tokens, PINs, dados biométricos) sejam protegidos usando mecanismos certificados do sistema operacional:

- Android: **Android Keystore System** (hardware-backed quando disponível)
- iOS: **Secure Enclave** + **Keychain**

Com React Native, o acesso a esses mecanismos passa por uma **bridge JavaScript** — uma camada adicional que não faz parte das certificações nativas. Isso pode gerar questionamentos em auditorias.

Com KMP, o acesso é **direto ao código nativo** — auditores veem Kotlin/Swift chamando as APIs certificadas, sem intermediários.

### Risco 2: Atualizações de Sistema Operacional

A cada versão major do Android e iOS, mudanças na API de biometria, keychain e notificações historicamente quebraram funcionalidades em apps React Native (exemplos: iOS 14 notificações, Android 12 splash screen, iOS 15 privacy manifests).

Com UI nativa, o time já tem o processo de compatibilidade dominado — é o que fazem hoje. O impacto de atualização de SO no módulo shared é mínimo, pois a lógica de negócio não depende de APIs de plataforma.

### Risco 3: Performance em Operações Críticas

| Operação | React Native | UI Nativa |
|---|---|---|
| Leitura de biometria | Via bridge (~50–200ms overhead) | Direto na API (~5–20ms) |
| Animação de confirmação | JS thread + shadow thread | GPU nativo |
| Câmera (QR Code PIX) | Bridge para módulo nativo | Acesso direto (AVFoundation/ML Kit) |
| Carregamento inicial | JS bundle parse (~300–800ms) | Compilado nativo (~50–150ms) |

Em um app bancário, latência percebida em biometria e confirmações afeta diretamente a confiança do usuário na segurança do produto.

---

## O que o time perde com React Native

| Expertise | Status com RN |
|---|---|
| Jetpack Compose (Android) | Inutilizado |
| SwiftUI (iOS) | Inutilizado |
| Android Keystore + Biometrics | Acessado via bridge third-party |
| iOS Secure Enclave + Face ID | Acessado via bridge third-party |
| Otimizações de performance nativa | Impossível sem módulo nativo |
| Publicação na Play Store / App Store | Reaprendizado de tooling |
| Debugging nativo (Android Studio / Xcode) | Substituído por Metro + Flipper |

**Anos de expertise em ferramentas premium** (Android Studio Profiler, Instruments do Xcode, Xcode Organizer para crash reports) seriam trocados por um conjunto de ferramentas menos maduro para contexto bancário.

---

## O que o time ganha com KMP + UI Nativa

1. **Lógica de negócio escrita uma vez** — bug no validador de CPF corrigido em um único lugar
2. **Testes unitários que rodam em Android e iOS** — maior confiança no código compartilhado
3. **White-label real** — configuração de tema e feature flags no shared, aplicados nas UIs nativas
4. **Curva de adoção gradual** — KMP pode ser adotado módulo a módulo, sem rewrite total
5. **Preservação de know-how** — time continua produtivo desde o primeiro dia
6. **Caminho para o futuro** — quando Compose Multiplatform para iOS amadurecer, o shared já está pronto

---

## Adoção no Mercado

Empresas que usam KMP com UI nativa em produção (2025–2026):

| Empresa | Produto | Escala |
|---|---|---|
| **Cash App** (Block) | Pagamentos P2P | Dezenas de milhões de usuários |
| **Netflix** | Features de discovery | App global |
| **1Password** | Gerenciador de senhas | Produto de segurança crítica |
| **VMware** | Apps corporativos | Grandes empresas |
| **Philips** | HealthSuite | Setor regulado (saúde) |
| **Touchlab** | Banking clients | Setor financeiro |

**1Password** e os **clientes de Touchlab no setor bancário** são especialmente relevantes — são produtos de **segurança crítica** em setores **regulados**, exatamente como o nosso contexto.

---

## Custo de Migração

### Rota React Native
- Rewrite total de 2 apps
- Contratação ou requalificação de toda a equipe em RN + JS/TypeScript
- Perda de produtividade estimada: 12–18 meses até atingir paridade de features
- Risco de entrega: alto (nova stack, novo time, novo paradigma)

### Rota KMP + UI Nativa (proposta)
- Migração incremental: shared KMP pode ser adicionado ao projeto atual módulo a módulo
- Time começa pelo módulo de dados e domínio (o que já conhecem em Kotlin)
- iOS devs aprendem a consumir o shared (leitura de Kotlin básico, não escrita)
- Risco de entrega: baixo (time continua trabalhando na stack dominada)
- Feature parity com o app atual mantida durante a migração

---

## Conclusão

A proposta KMP + UI Nativa não é um desvio da modernização — é uma modernização mais inteligente para o contexto de um banco regulado pelo Banco Central. Ela entrega os requisitos de cross-platform e white-label com menor risco, preserva o know-how do time, e usa tecnologia com adoção comprovada em produtos de segurança crítica.

**Próximo passo:** Avaliação desta POC com as 4 features implementadas — Login com Biometria, Saldo e Extrato, PIX e White-label — demonstrando os benefícios em código funcionando.
