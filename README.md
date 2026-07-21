# mobile-one

> One codebase. Native experience.

Prova de conceito técnica para avaliar **Kotlin Multiplatform (KMP) com interfaces nativas** como arquitetura cross-platform para aplicativos bancários com suporte a white-label.

O projeto valida o compartilhamento das camadas de domínio, dados, configuração e segurança, mantendo as interfaces em Jetpack Compose no Android e SwiftUI no iOS.

## Visão Geral

A arquitetura proposta separa as responsabilidades por camada:

| Camada | Tecnologia | Responsabilidade |
|---|---|---|
| Shared | Kotlin Multiplatform | Domínio, dados, validações, configuração, contratos de segurança |
| Android | Jetpack Compose | Interface nativa e integração com APIs Android |
| iOS | SwiftUI | Interface nativa e integração com APIs iOS |
| HTTP | Ktor | Cliente HTTP compartilhado |
| Persistência | SQLDelight | Schema e queries compartilhados com drivers nativos |
| DI | Koin | Injeção de dependência no módulo compartilhado |
| Assíncrono | Coroutines + Flow | Estado reativo e operações assíncronas |

## Telas Implementadas

As telas abaixo demonstram a aplicação dos mesmos contratos compartilhados em três configurações de marca.

### Splash

![Splash - 3 marcas](docs/assets/screenshots/splash-brands.png)

### Login

![Login - 3 marcas](docs/assets/screenshots/login-brands.png)

### Biometria

![Biometria - 3 marcas](docs/assets/screenshots/biometria-brands.png)

### Home / Conta

![Home Conta - 3 marcas](docs/assets/screenshots/home-conta-brands.png)

### Home / Cartões

![Home Cartões - 3 marcas](docs/assets/screenshots/home-cartoes-brands.png)

### Brand Switcher

![Brand Switcher - 3 marcas](docs/assets/screenshots/brand-switcher-brands.png)

## Estrutura do Projeto

```text
mobile-one/
├── docs/
│   ├── adr/                # Architecture Decision Records
│   ├── assets/screenshots/ # Evidências visuais da POC
│   ├── confluence/         # Páginas base para documentação corporativa
│   ├── figma/              # Mapeamento de referências visuais
│   └── specs/              # Especificações técnicas das features
├── shared/                 # Kotlin Multiplatform: dados, domínio e contratos
├── androidApp/             # Android: Jetpack Compose
└── iosApp/                 # iOS: SwiftUI
```

## Documentação

### Páginas para Confluence

Os arquivos em `docs/confluence/` foram preparados para publicação como páginas de documentação técnica:

| Página | Objetivo |
|---|---|
| [Índice](docs/confluence/00-indice.md) | Estrutura sugerida do espaço no Confluence |
| [Visão geral](docs/confluence/01-visao-geral.md) | Contexto, escopo e objetivos da prova de conceito |
| [Comparativo técnico](docs/confluence/02-comparativo-kmp-ui-nativa-vs-react-native.md) | Avaliação técnica entre KMP com UI nativa e React Native |
| [Arquitetura](docs/confluence/03-arquitetura-tecnica.md) | Organização de módulos, contratos e fluxo de dependências |
| [Evidências e métricas](docs/confluence/04-evidencias-e-metricas.md) | Resultados medidos, testes e evidências visuais |
| [Roadmap de adoção](docs/confluence/05-roadmap-de-adocao.md) | Estratégia incremental para evolução da arquitetura |

### ADRs

| ADR | Decisão | Status |
|---|---|---|
| [ADR-001](docs/adr/ADR-001-kmp-vs-react-native.md) | KMP com UI nativa para compartilhamento de dados e domínio | Aceito |
| [ADR-002](docs/adr/ADR-002-sqldelight-persistencia.md) | SQLDelight como persistência compartilhada | Aceito |
| [ADR-003](docs/adr/ADR-003-ktor-http-client.md) | Ktor como HTTP client compartilhado | Aceito |
| [ADR-004](docs/adr/ADR-004-white-label-strategy.md) | Estratégia white-label via configuração compartilhada | Aceito |
| [ADR-005](docs/adr/ADR-005-biometria-expect-actual.md) | Biometria e segurança via `expect/actual` | Aceito |

### Specs

| Spec | Feature | Status |
|---|---|---|
| [SPEC-001](docs/specs/SPEC-001-login-biometrico.md) | Login com autenticação biométrica | Aprovado |
| [SPEC-002](docs/specs/SPEC-002-saldo-extrato.md) | Saldo e extrato de conta | Aprovado |
| [SPEC-003](docs/specs/SPEC-003-pix.md) | Transferência PIX | Aprovado |
| [SPEC-004](docs/specs/SPEC-004-white-label-config.md) | Configuração white-label | Aprovado |
| [SPEC-005](docs/specs/SPEC-005-design-system-tokens.md) | Tokens do design system | Aprovado |
| [SPEC-006](docs/specs/SPEC-006-splash-layout.md) | Layout da Splash Screen | Aprovado |
| [SPEC-007](docs/specs/SPEC-007-login-layout.md) | Layout de Login | Aprovado |
| [SPEC-008](docs/specs/SPEC-008-biometria-layout.md) | Layout de Biometria | Aprovado |
| [SPEC-009](docs/specs/SPEC-009-home-cartoes-layout.md) | Layout de Home / Cartões | Aprovado |
| [SPEC-010](docs/specs/SPEC-010-home-conta-layout.md) | Layout de Home / Conta | Aprovado |
| [SPEC-011](docs/specs/SPEC-011-brand-switcher-layout.md) | Layout de Brand Switcher | Aprovado |

## Métricas Técnicas

Medição reproduzível:

```bash
python3 scripts/measure_code_metrics.py
./gradlew :shared:allTests
```

Snapshot técnico da POC:

| Indicador | Resultado |
|---|---:|
| Módulo `shared` sobre o app total | 20,6% das linhas de código de produção |
| `commonMain` dentro do módulo `shared` | 68,2% |
| Lógica de negócio em `commonMain` | 100% |
| Use cases e validadores compartilhados | 100% |
| Testes no `shared` | 88 |
| Falhas na suíte compartilhada | 0 |
| Plataformas testadas | Android JVM e iOS Simulator |

A métrica principal desta arquitetura não é maximizar o percentual total do repositório em código compartilhado. O objetivo é compartilhar as regras que devem permanecer consistentes entre plataformas, preservando UI e integrações nativas onde elas agregam mais valor.

## Como Rodar

### Pré-requisitos

- JDK 17
- Android Studio com Android SDK 36 e `ANDROID_HOME` configurado
- Xcode com simulador iOS 17+
- [XcodeGen](https://github.com/yonaskolb/XcodeGen): `brew install xcodegen`

### Validar o módulo compartilhado

```bash
./gradlew :shared:build
./gradlew :shared:allTests
```

### Android

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

Também é possível abrir a raiz do projeto no Android Studio, selecionar a configuração `androidApp` e executar em um emulador ou dispositivo físico.

### iOS

```bash
cd iosApp
xcodegen generate
open iosApp.xcodeproj
```

No Xcode, selecione o scheme `iosApp` e execute em um simulador iOS 17+.

Build pelo terminal:

```bash
cd iosApp
xcodegen generate
xcodebuild -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build
```

O target iOS possui uma fase de build que executa `./gradlew :shared:embedAndSignAppleFrameworkForXcode`, embutindo o framework KMP mais recente no app.

## Workflow de Desenvolvimento

1. Escrever ou revisar a spec em `docs/specs/` antes de implementar.
2. Definir contratos no `shared/commonMain`.
3. Implementar domínio, dados e contratos `expect/actual` no `shared`.
4. Adicionar testes em `shared/commonTest`.
5. Implementar UI Android em Compose consumindo os contratos compartilhados.
6. Implementar UI iOS em SwiftUI consumindo os mesmos contratos.
7. Validar as evidências funcionais e atualizar a documentação aplicável.

## Referências de Design

As referências visuais devem ser registradas em `docs/figma/design-system.md`. O arquivo não deve conter links pessoais ou temporários; ao migrar para o ambiente corporativo, preencher com o file key e os node IDs oficiais do Figma da empresa.
