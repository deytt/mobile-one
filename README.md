# mobile-one

> *One codebase. Native experience.*

POC técnica para demonstrar que **Kotlin Multiplatform (KMP) com UI nativa** é uma alternativa superior ao React Native para um aplicativo bancário cross-platform e white-label.

---

## Telas do app (white-label)

As mesmas telas, nas **3 marcas** da POC — Banco Principal (`#003B6F`), Fintech Verde (`#00A86B`) e Banco Premium (`#782D00`). Designs de referência no [Figma Mobile-One](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One).

| Marca | Primary | Tipografia | Radius |
|---|---|---|---|
| Banco Principal | `#003B6F` | Roboto | 12dp |
| Fintech Verde | `#00A86B` | Inter | 16dp |
| Banco Premium | `#782D00` | Georgia | 4dp |

### Splash

![Splash — 3 marcas](docs/assets/screenshots/splash-brands.png)

### Login

![Login — 3 marcas](docs/assets/screenshots/login-brands.png)

### Biometria

![Biometria — 3 marcas](docs/assets/screenshots/biometria-brands.png)

### Home / Conta

![Home Conta — 3 marcas](docs/assets/screenshots/home-conta-brands.png)

### Home / Cartões

![Home Cartões — 3 marcas](docs/assets/screenshots/home-cartoes-brands.png)

### Brand Switcher

![Brand Switcher — 3 marcas](docs/assets/screenshots/brand-switcher-brands.png)

---

## Estrutura do projeto

```
mobile-one/
├── .cursor/rules/          # Contexto do projeto para agentes de IA (Memory Bank)
├── docs/
│   ├── assets/screenshots/ # Previews das telas (3 marcas) para o README
│   ├── adr/                # Architecture Decision Records — decisões e argumentos
│   ├── specs/              # Especificações de features e contratos de interface
│   └── poc-pitch/          # Material de apresentação + métricas reais
├── shared/                 # KMP — Kotlin Multiplatform (dados + domínio)
├── androidApp/             # Android — Jetpack Compose
└── iosApp/                 # iOS — SwiftUI
```

## Documentação

### Architecture Decision Records (ADRs)

As decisões arquiteturais da POC ficam em `docs/adr/`. Elas explicam o racional técnico usado no pitch para comparar KMP + UI nativa com React Native.

| ADR | Decisão | Por que importa para a POC | Status |
|---|---|---|---|
| [ADR-001](docs/adr/ADR-001-kmp-vs-react-native.md) | KMP com UI nativa vs React Native | Define a tese central: compartilhar dados/domínio e preservar UX nativa | Aceito |
| [ADR-002](docs/adr/ADR-002-sqldelight-persistencia.md) | SQLDelight como persistência compartilhada | Mantém schema e queries em Kotlin Multiplatform, com drivers nativos | Aceito |
| [ADR-003](docs/adr/ADR-003-ktor-http-client.md) | Ktor como HTTP client compartilhado | Evita duplicar camada de rede entre Android e iOS | Aceito |
| [ADR-004](docs/adr/ADR-004-white-label-strategy.md) | Estratégia white-label via shared config | Centraliza marcas, tokens e feature flags no `shared` | Aceito |
| [ADR-005](docs/adr/ADR-005-biometria-expect-actual.md) | Biometria e segurança via `expect/actual` | Usa APIs nativas para Face ID/Touch ID, BiometricPrompt e armazenamento seguro | Aceito |

### Specs de Features (POC)

| Spec | Feature | Status |
|---|---|---|
| [SPEC-001](docs/specs/SPEC-001-login-biometrico.md) | Login com Autenticação Biométrica | Aprovado |
| [SPEC-002](docs/specs/SPEC-002-saldo-extrato.md) | Saldo e Extrato de Conta | Aprovado |
| [SPEC-003](docs/specs/SPEC-003-pix.md) | Transferência PIX | Aprovado |
| [SPEC-004](docs/specs/SPEC-004-white-label-config.md) | Demonstração White-Label | Aprovado |

### Material de Apresentação

- [Comparativo KMP vs React Native](docs/poc-pitch/comparativo-kmp-vs-react-native.md)
- [Roadmap de Migração](docs/poc-pitch/roadmap-migracao.md)
- [Métricas da POC (guia)](docs/poc-pitch/metricas-poc.md)
- [Métricas reais — snapshot 2026-07-21](docs/poc-pitch/metricas-resultados.md)

## Métricas de código (2026-07-21)

Medição reproduzível: `python3 scripts/measure_code_metrics.py` · testes: `./gradlew :shared:allTests`.

Detalhamento completo em [docs/poc-pitch/metricas-resultados.md](docs/poc-pitch/metricas-resultados.md).

### Compartilhamento

| Métrica | Valor |
|---|---|
| Módulo `shared` / app total (LOC code) | **20,6%** (2 220 / 10 765) |
| Só `commonMain` / app total | **14,1%** (1 514 / 10 765) |
| `commonMain` dentro do módulo `shared` | **68%** |
| Lógica de negócio (domain + data + config) | **100%** em `commonMain` |
| Use cases + validadores | **100%** compartilhados |

| Source set | Arquivos | LOC (code) |
|---|---:|---:|
| `shared/commonMain` | 64 | 1 514 |
| `shared/androidMain` + `iosMain` | 18 | 706 |
| `androidApp` (Compose) | 40 | 5 179 |
| `iosApp` (SwiftUI) | 36 | 3 366 |

O % do app total fica abaixo da meta antiga de “65–80%” porque a **UI é 100% nativa** (Compose/SwiftUI) e concentra a maior parte das linhas — exatamente a tese do [ADR-001](docs/adr/ADR-001-kmp-vs-react-native.md): compartilhar regras de negócio, não a UI.

### Cobertura de testes (shared)

| Indicador | Valor |
|---|---|
| Testes em `commonTest` | **88** |
| Falhas | **0** |
| Plataformas | Android JVM + iOS Simulator (mesma suíte) |
| Use cases com teste dedicado | **9 / 14 (64%)** |
| White-label / config | **100%** dos arquivos com teste |
| Validadores | Cobertura alta via `AuthValidatorTest` + `ValidatePixKeyUseCaseTest` |

Lacunas prioritárias: `ExecutePixTransferUseCase`, `DetectPixKeyTypeUseCase`, `LookupPixRecipientUseCase`, `RefreshAccountDataUseCase`, `ToggleBalanceVisibilityUseCase`.

## Stack

| Camada | Tecnologia |
|---|---|
| Shared (dados + domínio) | Kotlin Multiplatform (KMP) |
| Android UI | Jetpack Compose |
| iOS UI | SwiftUI |
| HTTP client | Ktor |
| Persistência local | SQLDelight |
| Injeção de dependência | Koin |
| Async | Kotlin Coroutines + Flow |

## Como rodar o projeto

### Pré-requisitos

- JDK 17
- Android Studio com Android SDK 36 e `ANDROID_HOME` configurado
- Xcode com simulador iOS 17+
- [XcodeGen](https://github.com/yonaskolb/XcodeGen): `brew install xcodegen`

### 1. Validar o módulo compartilhado

Antes de subir os apps, valide que o `shared` compila e que a mesma suíte de testes roda nas duas plataformas:

```bash
./gradlew :shared:build
./gradlew :shared:allTests
```

### 2. Subir o app Android

Via terminal:

```bash
# Gerar APK debug
./gradlew :androidApp:assembleDebug

# Instalar em emulador/dispositivo conectado
./gradlew :androidApp:installDebug
```

Via Android Studio:

1. Abra a raiz do repositório `mobile-one/`.
2. Aguarde o sync do Gradle terminar.
3. Selecione a configuração `androidApp`.
4. Escolha um emulador ou dispositivo físico.
5. Clique em Run.

### 3. Subir o app iOS

O projeto Xcode é gerado a partir de `iosApp/project.yml`, então gere o `.xcodeproj` antes de abrir no Xcode:

```bash
cd iosApp
xcodegen generate
open iosApp.xcodeproj
```

No Xcode:

1. Selecione o scheme `iosApp`.
2. Escolha um simulador iOS 17+, por exemplo `iPhone 17 Pro`.
3. Clique em Run.

Também é possível buildar pelo terminal:

```bash
cd iosApp
xcodegen generate
xcodebuild -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build
```

O target iOS possui uma fase de build que executa `./gradlew :shared:embedAndSignAppleFrameworkForXcode` automaticamente, embutindo o framework KMP `shared` mais recente no app.

### 4. Comandos úteis

```bash
# Métricas de compartilhamento de código
python3 scripts/measure_code_metrics.py

# Testes do shared nas plataformas configuradas
./gradlew :shared:allTests

# Build Android debug
./gradlew :androidApp:assembleDebug

# Regenerar projeto iOS
cd iosApp && xcodegen generate
```

## Workflow de desenvolvimento

1. Escrever/revisar a **spec** em `docs/specs/` antes de qualquer código
2. Definir contratos no `shared/commonMain` (interfaces, expect)
3. Implementar no shared (use cases, repositórios, actual)
4. Escrever testes do shared (rodam em Android e iOS)
5. Criar telas no **Figma Make** → importar para Figma Design
6. Implementar UI via **MCP do Figma** em Compose (Android) e SwiftUI (iOS)

## Contexto

Este projeto é uma POC criada pelo time de desenvolvimento Android/iOS de um banco para demonstrar aos gestores que existe uma alternativa ao React Native que:

- Compartilha **100% da lógica de negócio** (domain + data + config em `commonMain`); o módulo `shared` representa ~**21%** do LOC do app com UI nativa já implementada
- Mantém **performance 100% nativa** sem bridges JavaScript
- Preserva o **know-how técnico** do time atual
- Suporta **white-label** via configuração no shared
- É viável para um **app bancário regulado** pelo Banco Central
- Valida a lógica com **88 testes** que rodam iguais em Android e iOS
