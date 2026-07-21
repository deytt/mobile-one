# Arquitetura Técnica

## Visão de módulos

```text
mobile-one/
├── shared/
│   ├── commonMain/      # Código compartilhado: domínio, dados, config e contratos
│   ├── androidMain/     # Implementações Android de contratos expect/actual
│   ├── iosMain/         # Implementações iOS de contratos expect/actual
│   └── commonTest/      # Testes compartilhados
├── androidApp/          # UI Android em Jetpack Compose
└── iosApp/              # UI iOS em SwiftUI
```

## Regras de dependência

- `shared/domain` não depende de `shared/data`.
- `shared` não depende de `androidApp` nem de `iosApp`.
- `androidApp` e `iosApp` dependem de `shared`.
- A UI não contém regra de negócio; ela observa estado e dispara ações.
- Integrações nativas são encapsuladas por contratos `expect/actual`.

## Camada shared

A camada compartilhada contém a parte que deve ser consistente entre plataformas:

| Área | Exemplos |
|---|---|
| Entidades | Conta, transação, autenticação, transferência PIX |
| Use cases | Login, validação PIX, consulta de conta, troca de marca |
| Repositórios | Interfaces de dados e implementações fake/local/remotas |
| Validações | CPF, CNPJ, e-mail, chave aleatória, limites |
| Configuração | `WhiteLabelConfig`, tokens, feature flags |
| Segurança | Contratos de biometria, storage seguro e integridade |

## Contratos para UI nativa

O `shared` expõe contratos simples para Android e iOS:

- `StateFlow<UiState>` para estado observável;
- `suspend fun` para ações;
- sealed classes para erros e navegação;
- modelos de apresentação independentes de plataforma.

O módulo compartilhado não expõe `Context`, `UIViewController`, `View`, `Activity` ou qualquer tipo específico de UI.

## Padrão `expect/actual`

Recursos específicos de plataforma são modelados por contratos comuns:

```kotlin
expect class BiometricAuthenticator {
    suspend fun authenticate(reason: String): BiometricResult
}
```

Android implementa com APIs como `BiometricPrompt` e Android Keystore. iOS implementa com `LocalAuthentication`, Keychain e Secure Enclave quando aplicável.

Esse padrão também é utilizado para storage seguro, integridade do dispositivo, scanner de QR Code e engine HTTP.

## White-label

A configuração white-label é centralizada em `shared/commonMain`:

- `brandId` e `brandName`;
- tokens de cor;
- tipografia;
- radius;
- assets de logo;
- feature flags;
- dados de suporte;
- configuração de onboarding.

Android e iOS mapeiam os mesmos tokens para seus sistemas nativos de tema. Isso evita hardcode de marca na UI e permite validar múltiplas identidades visuais com a mesma lógica de negócio.

## Fluxo de dados

```text
UI Android / UI iOS
        │
        ▼
ViewModel / ObservableObject nativo
        │
        ▼
Use cases compartilhados
        │
        ├── Repositórios compartilhados
        ├── Validadores compartilhados
        ├── Configuração white-label
        └── Contratos expect/actual
```

## Testes

A suíte `commonTest` valida os contratos compartilhados. O mesmo teste executa para Android JVM e iOS Simulator, reduzindo risco de divergência entre plataformas.

Áreas já cobertas na POC:

- autenticação;
- validação de CPF e PIX;
- configuração white-label;
- casos de uso de conta;
- formatação monetária;
- troca de marca;
- parsing de QR Code PIX.
