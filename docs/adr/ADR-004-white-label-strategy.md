# ADR-004: Estratégia White-Label via Configuração no Shared

**Status:** Aceito  
**Data:** 2026-07-17  
**Autores:** Time de Desenvolvimento Mobile

---

## Contexto

Um dos requisitos centrais da reescrita do aplicativo é suporte a **white-label** — a capacidade de o banco (ou instituições parceiras) customizar a aparência e o comportamento do app sem alterar o código-fonte, apenas fornecendo uma configuração.

Casos de uso concretos:
- Banco lança um produto digital sob uma marca diferente (ex: fintech parceira)
- Banco tem sub-produtos com identidades visuais distintas (ex: conta PJ vs PF)
- Contrato B2B onde o banco oferece o "app como serviço" para outras instituições

---

## Decisão

Implementar white-label em **duas camadas separadas e independentes**:

1. **`WhiteLabelConfig` no `shared`** — configuração de comportamento, feature flags e tokens de tema
2. **Aplicação de tema nas UIs nativas** — Compose Theme (Android) e SwiftUI Theme (iOS) consomem os tokens do shared

---

## Arquitetura da Solução

### Camada shared — WhiteLabelConfig

```kotlin
// shared/commonMain/config/WhiteLabelConfig.kt
data class WhiteLabelConfig(
    val brandId: String,                    // identificador único do cliente
    val brandName: String,                  // "Banco Exemplo" ou "Fintech X"
    val logoUrl: String,                    // URL do logo (CDN do banco)
    val theme: ThemeTokens,
    val features: FeatureFlags,
    val supportContact: SupportContact
)

data class ThemeTokens(
    val colorPrimary: String,               // hex, ex: "#0066CC"
    val colorSecondary: String,
    val colorBackground: String,
    val colorSurface: String,
    val colorError: String,
    val colorOnPrimary: String,
    val fontFamily: FontFamily,             // sealed class
    val borderRadius: BorderRadius          // sealed class: Sharp, Medium, Rounded
)

data class FeatureFlags(
    val pixEnabled: Boolean = true,
    val investmentsEnabled: Boolean = false,
    val creditCardEnabled: Boolean = true,
    val openFinanceEnabled: Boolean = true,
    val biometricLoginEnabled: Boolean = true,
    val cardVirtualEnabled: Boolean = true
)
```

### Carregamento da configuração

```kotlin
// shared/commonMain/config/WhiteLabelConfigRepository.kt
interface WhiteLabelConfigRepository {
    suspend fun loadConfig(brandId: String): Result<WhiteLabelConfig>
}

// Estratégias de carregamento:
// 1. Bundled no app (default brand) — arquivo JSON em resources
// 2. Remote (CDN do banco) — para multi-brand no mesmo binário
// 3. Build-time via Gradle (um apk por marca) — white-label clássico
```

### Android — aplicando os tokens no Compose Theme

```kotlin
// androidApp/ui/theme/BankTheme.kt
@Composable
fun BankTheme(
    config: WhiteLabelConfig,
    content: @Composable () -> Unit
) {
    val colorScheme = config.theme.toMaterialColorScheme()
    val typography = config.theme.fontFamily.toTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
```

### iOS — aplicando os tokens no SwiftUI Environment

```swift
// iosApp/Theme/BankTheme.swift
struct BankTheme: EnvironmentKey {
    static let defaultValue = ThemeTokens.default
}

extension EnvironmentValues {
    var bankTheme: ThemeTokens {
        get { self[BankTheme.self] }
        set { self[BankTheme.self] = newValue }
    }
}
```

---

## Estratégias de Distribuição White-Label

### Opção 1: Um binário, múltiplos temas (Remote Config)
- Configuração carregada de servidor ao iniciar o app
- Ideal para: sub-produtos do mesmo banco (ex: PF vs PJ)
- Risco: falha na rede no primeiro acesso

### Opção 2: Build por cliente (Build-time Config)
- Cada cliente gera um APK/IPA diferente com configuração bundled
- Ideal para: contratos B2B (app com a marca do parceiro)
- Risco: manutenção de múltiplos pipelines de CI/CD

### Opção 3: Híbrido (RECOMENDADO para POC)
- Default config bundled (garante funcionamento offline)
- Override remoto opcional ao conectar
- Demonstra ambas as capacidades na POC

---

## O que NÃO é white-label nesta arquitetura

- Lógica de negócio diferente por cliente → isso é feature flag, não tema
- Fluxos de usuário completamente diferentes → isso é uma feature separada
- Dados de terceiros (ex: outra bandeira de cartão) → integração de API, não white-label

---

## Consequências

### Positivas
- A mesma base de código serve múltiplos clientes sem fork
- Feature flags permitem habilitar/desabilitar funcionalidades por cliente sem novo build (Opção 1)
- Tema aplicado de forma centralizada — sem cores hardcoded espalhadas pelo código de UI
- Demonstra ao gestor que o objetivo de white-label é atingível com esta arquitetura

### Negativas / Trade-offs aceitos
- Remote Config (Opção 1) requer estratégia de fallback robusta para o app não ficar sem tema
- Testes de UI precisam rodar com diferentes configurações para garantir que nenhum tema "quebra" o layout
- Assets visuais (ícones, imagens) precisam de estratégia separada (CDN vs bundle)
