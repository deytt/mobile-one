# Visão Geral - mobile-one

## Resumo

O **mobile-one** é uma prova de conceito para avaliar Kotlin Multiplatform (KMP) com interfaces nativas como alternativa arquitetural para aplicativos bancários cross-platform e white-label.

A proposta combina compartilhamento de código nas camadas em que a consistência funcional é essencial com interfaces nativas em cada plataforma:

- **Shared KMP:** domínio, dados, validações, configuração white-label e contratos de segurança.
- **Android:** UI nativa em Jetpack Compose.
- **iOS:** UI nativa em SwiftUI.

## Objetivos técnicos

- Reduzir duplicidade de regras de negócio entre Android e iOS.
- Preservar experiência nativa, acessibilidade e performance de cada plataforma.
- Centralizar validações sensíveis, como autenticação, PIX e regras de domínio.
- Permitir evolução white-label por configuração, tokens e feature flags.
- Validar testes compartilhados executando a mesma suíte em Android e iOS.
- Avaliar caminho incremental de adoção sem exigir substituição completa das UIs nativas.

## Escopo validado

A POC contempla os seguintes fluxos e componentes:

| Área | Validação |
|---|---|
| Autenticação | Login por credenciais e biometria com contratos compartilhados |
| Conta | Estado de conta, saldo, extrato e formatação monetária |
| Cartões | Tela nativa consumindo tokens e navegação consistente |
| PIX | Validação de chave, QR Code, revisão e comprovante |
| White-label | Catálogo de marcas, tokens, feature flags e troca de marca em build interno |
| Segurança | `expect/actual` para recursos nativos como biometria, storage e integridade |
| Testes | Suíte `commonTest` executada em Android JVM e iOS Simulator |

## Princípio arquitetural

A arquitetura não busca compartilhar toda a aplicação. O foco é compartilhar a lógica que deve permanecer igual entre plataformas, mantendo nativa a camada que depende diretamente da experiência de usuário, do sistema operacional e dos guidelines de cada ecossistema.

Esse modelo reduz divergência de regras de negócio sem abrir mão de recursos nativos, ferramentas de profiling e padrões de publicação já consolidados em Android e iOS.

## Stack da POC

| Camada | Tecnologia |
|---|---|
| Shared | Kotlin Multiplatform |
| Android UI | Jetpack Compose |
| iOS UI | SwiftUI |
| HTTP | Ktor |
| Persistência local | SQLDelight |
| Injeção de dependência | Koin |
| Estado assíncrono | Kotlin Coroutines + Flow |

## Entregáveis relacionados

- ADRs em `docs/adr/`
- Specs técnicas em `docs/specs/`
- Evidências visuais em `docs/assets/screenshots/`
- Métricas reproduzíveis via `scripts/measure_code_metrics.py`
