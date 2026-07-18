# SPEC-004: Demonstração White-Label

**Status:** Aprovado  
**Versão:** 1.0  
**Data:** 2026-07-17  
**Feature owner:** Time Mobile  
**Figma:**
- Brand Switcher (Dev Mode): [`29:23293`](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-23293&m=dev)
- Design System (tokens das 3 marcas): [`docs/figma/design-system.md`](../figma/design-system.md)

---

## Objetivo

Demonstrar de forma visual e técnica que a arquitetura KMP suporta white-label — a capacidade de o mesmo app aparecer com identidades visuais e conjuntos de features diferentes, sem alterar uma linha de código de UI ou lógica de negócio.

Esta spec é especialmente pensada para a apresentação aos gestores: o demonstrador troca a configuração ao vivo, e o app muda completamente de aparência em segundos.

---

## Escopo da demonstração

A POC incluirá **3 configurações de marca** pré-criadas:

| Marca | Cor Primária | Cor Secundária | Features ativas |
|---|---|---|---|
| **Banco Principal** | `#003B6F` (azul naval) | `#F7941D` (laranja) | Todas |
| **Fintech Verde** | `#00A86B` (verde esmeralda) | `#1A1A2E` (azul escuro) | PIX, Extrato (sem Investimentos) |
| **Banco Premium** | `#8B0000` (vermelho escuro) | `#C9A84C` (dourado) | Todas + flag VIP |

---

## Contratos do shared (KMP)

### Estrutura completa do WhiteLabelConfig

```kotlin
// shared/commonMain/config/WhiteLabelConfig.kt
data class WhiteLabelConfig(
    val brandId: String,
    val brandName: String,
    val logoAsset: String,                  // nome do asset no bundle local
    val logoUrl: String?,                   // URL para logo remoto (opcional)
    val theme: ThemeTokens,
    val features: FeatureFlags,
    val supportContact: SupportContact,
    val onboarding: OnboardingConfig
)

data class ThemeTokens(
    val colorPrimary: String,
    val colorPrimaryVariant: String,
    val colorSecondary: String,
    val colorBackground: String,
    val colorSurface: String,
    val colorError: String,
    val colorOnPrimary: String,
    val colorOnBackground: String,
    val fontFamilyName: String,             // "Inter", "Roboto", "SF Pro" (fallback por plataforma)
    val borderRadiusDp: Int,               // 0 = sharp, 8 = medium, 24 = rounded
    val elevationEnabled: Boolean
)

data class FeatureFlags(
    val pixEnabled: Boolean = true,
    val scheduledPixEnabled: Boolean = true,
    val investmentsEnabled: Boolean = false,
    val creditCardEnabled: Boolean = true,
    val openFinanceEnabled: Boolean = true,
    val biometricLoginEnabled: Boolean = true,
    val virtualCardEnabled: Boolean = true,
    val cashbackEnabled: Boolean = false,
    val isVipClient: Boolean = false
)

data class SupportContact(
    val phone: String,
    val whatsappNumber: String?,
    val email: String,
    val chatEnabled: Boolean
)

data class OnboardingConfig(
    val welcomeTitle: String,
    val welcomeSubtitle: String,
    val backgroundAsset: String
)
```

### Repositório de configuração

```kotlin
// shared/commonMain/config/WhiteLabelConfigRepository.kt
interface WhiteLabelConfigRepository {
    suspend fun loadConfig(brandId: String): Result<WhiteLabelConfig>
    fun observeConfig(): Flow<WhiteLabelConfig>
}

// Implementação para a POC: carrega de JSON bundled no assets
class BundledWhiteLabelConfigRepository : WhiteLabelConfigRepository {
    override suspend fun loadConfig(brandId: String): Result<WhiteLabelConfig> {
        val json = loadJsonAsset("white_label/$brandId.json")
        return parseConfig(json)
    }
}
```

### Use Case

```kotlin
class SwitchBrandUseCase(
    private val configRepository: WhiteLabelConfigRepository,
    private val appStateRepository: AppStateRepository
) {
    suspend operator fun invoke(brandId: String): Result<Unit> {
        return configRepository.loadConfig(brandId)
            .onSuccess { config -> appStateRepository.setCurrentConfig(config) }
            .map { }
    }
}
```

---

## Como o tema é aplicado na UI nativa

### Android (Compose)

```kotlin
// androidApp/ui/theme/BankTheme.kt
@Composable
fun BankTheme(
    config: WhiteLabelConfig = LocalWhiteLabelConfig.current,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialColorScheme(
        primary = Color(android.graphics.Color.parseColor(config.theme.colorPrimary)),
        secondary = Color(android.graphics.Color.parseColor(config.theme.colorSecondary)),
        background = Color(android.graphics.Color.parseColor(config.theme.colorBackground)),
        // ...
    )
    val shapes = Shapes(
        small = RoundedCornerShape(config.theme.borderRadiusDp.dp / 2),
        medium = RoundedCornerShape(config.theme.borderRadiusDp.dp),
        large = RoundedCornerShape(config.theme.borderRadiusDp.dp * 2)
    )
    MaterialTheme(colorScheme = colorScheme, shapes = shapes, content = content)
}
```

### iOS (SwiftUI)

```swift
// iosApp/Theme/BankThemeEnvironment.swift
struct BankThemeKey: EnvironmentKey {
    static let defaultValue = ThemeTokens.bancoPrincipal
}

extension EnvironmentValues {
    var bankTheme: ThemeTokens {
        get { self[BankThemeKey.self] }
        set { self[BankThemeKey.self] = newValue }
    }
}

// Uso em qualquer View:
struct PrimaryButton: View {
    @Environment(\.bankTheme) var theme
    var body: some View {
        Button(...).background(Color(hex: theme.colorPrimary))
            .cornerRadius(CGFloat(theme.borderRadiusDp))
    }
}
```

---

## Feature Flags na UI

```kotlin
// Android — componente que respeita feature flags
@Composable
fun HomeQuickActions(config: WhiteLabelConfig) {
    Row {
        QuickActionButton("PIX", visible = config.features.pixEnabled)
        QuickActionButton("Investir", visible = config.features.investmentsEnabled)
        QuickActionButton("Cartão Virtual", visible = config.features.virtualCardEnabled)
    }
}
```

```swift
// iOS — equivalente SwiftUI
struct HomeQuickActions: View {
    let config: WhiteLabelConfig
    var body: some View {
        HStack {
            if config.features.pixEnabled { QuickActionButton("PIX") }
            if config.features.investmentsEnabled { QuickActionButton("Investir") }
            if config.features.virtualCardEnabled { QuickActionButton("Cartão Virtual") }
        }
    }
}
```

---

## Tela de demonstração (exclusiva da POC)

Para a apresentação aos gestores, a POC incluirá uma **tela de Developer Mode** (oculta em produção) que permite trocar a configuração de marca ao vivo:

```
[Dev Mode — Brand Switcher]

○ Banco Principal     [Preview: azul naval + laranja]
○ Fintech Verde       [Preview: verde + azul escuro]  
○ Banco Premium       [Preview: vermelho + dourado]

[Aplicar] → app reinicia com nova configuração instantaneamente
```

Esta tela demonstra o white-label de forma visceral para gestores não-técnicos.

---

## Arquivos de configuração bundled (JSON)

```
shared/commonMain/resources/
└── white_label/
    ├── banco_principal.json
    ├── fintech_verde.json
    └── banco_premium.json
```

---

## Critérios de aceite da POC

- [ ] Trocar de marca muda cores, bordas e nome do banco em toda a UI
- [ ] Feature flags ocultam/exibem botões de ação corretamente
- [ ] Logo carregado conforme a configuração da marca
- [ ] Tela de brand switcher funciona no Android e iOS
- [ ] A troca de marca não requer reinstalação nem novo build
- [ ] Todas as telas (Login, Home, PIX) respeitam o tema da marca ativa
- [ ] O código de UI não contém nenhuma cor ou nome de marca hardcoded
