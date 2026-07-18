# SPEC-005 — Design System: Tokens e Temas White-Label

**Status:** Pronto para implementação  
**Tipo:** Layout / Theme  
**Figma:** [Banco Principal](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-4371) · [Fintech Verde](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-4521) · [Banco Premium](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-4671)

---

## Objetivo

Garantir que `WhiteLabelConfig` e os temas nativos (Compose `MaterialTheme` e SwiftUI `EnvironmentValues`) reflitam fielmente os tokens extraídos do Figma para as 3 marcas da POC. Esta spec serve de **referência** para todos os outros specs de layout.

---

## Tokens por Marca

| Token | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| `colorPrimary` | `#003B6F` | `#00A86B` | `#7B2D00` |
| `colorSecondary` | `#F7941D` | `#1A1A2E` | `#C9A84C` |
| `colorBackground` | `#F5F7FA` | `#F0FAF5` | `#FAFAF8` |
| `colorSurface` | `#FFFFFF` | `#FFFFFF` | `#FFFFFF` |
| `colorOnPrimary` | `#FFFFFF` | `#FFFFFF` | `#FFFFFF` |
| `colorOnBackground` | `#1A1A2E` | `#1A1A2E` | `#1A1A1A` |
| `colorOnSurface` | `#6B7280` | `#6B7280` | `#6B6B6B` |
| `colorError` | `#DC2626` | `#EF4444` | `#B91C1C` |
| `fontFamily` | `Roboto` | `Inter` | `Georgia` |
| `borderRadiusDp` | `12` | `16` | `4` |
| `brandId` | `banco_principal` | `fintech_verde` | `banco_premium` |
| `brandName` | `Banco Principal` | `Fintech Verde` | `Banco Premium` |

### Tokens adicionais identificados no Figma

| Token | Valor |
|---|---|
| `spacing/spacing-4` | 4dp |
| `spacing/spacing-8` | 8dp |
| `spacing/spacing-12` | 12dp |
| `spacing/spacing-16` | 16dp |
| `spacing/spacing-20` | 20dp |
| `spacing/spacing-24` | 24dp |
| `spacing/spacing-32` | 32dp |
| `spacing/spacing-inset-divider-left` | 16dp |
| `line-height/l-height-16` | 16dp |
| `line-height/l-height-24` | 24dp |
| `font-size/f-size-12` | 12sp |
| `font-size/f-size-14` | 14sp |
| `font-size/f-size-16` | 16sp |
| `border-width/bordersmall` | 1dp |
| `border-width/bordermedium` | 2dp |

---

## BrandLogo — Identidade Visual

Cada marca possui um identificador visual exibido na barra superior e no Splash:

### Banco Principal
- Fundo: `colorPrimary` (#003B6F)  
- Container: `rgba(255,255,255,0.18)`, radius = `borderRadiusDp` (12dp)  
- Texto: iniciais "BP", Roboto Bold, branco  
- Nome: Roboto Bold  

### Fintech Verde
- Fundo: `colorPrimary` (#00A86B)  
- Container: `rgba(255,255,255,0.18)`, radius = `borderRadiusDp` (16dp, circular)  
- Texto: iniciais "FV", Inter SemiBold, branco  
- Nome: Inter SemiBold  

### Banco Premium
- Fundo: `colorPrimary` (#7B2D00)  
- Container: losango rotacionado 45°, `rgba(255,255,255,0.15)`, radius = 4dp  
- Texto: iniciais "BP", Georgia Bold Italic, branco  
- Nome: Georgia Bold Italic, letter-spacing +0.16  

---

## Android — Ajustes no Compose Theme

### Arquivo: `androidApp/ui/theme/`

#### `Color.kt` — atualizar/adicionar por brand:
- Certificar que cada `WhiteLabelConfig` tem um `ColorScheme` correspondente
- Aplicar `colorOnSurface` como cor de textos secundários
- `colorBackground` como `MaterialTheme.colorScheme.background`
- `colorSurface` como `MaterialTheme.colorScheme.surface`

#### `Shape.kt` — radius por brand:
```kotlin
// Banco Principal → CornerSize(12.dp)
// Fintech Verde   → CornerSize(16.dp)
// Banco Premium   → CornerSize(4.dp)
val brandShapes = Shapes(
    small = RoundedCornerShape(config.borderRadiusDp.dp),
    medium = RoundedCornerShape(config.borderRadiusDp.dp),
    large = RoundedCornerShape(config.borderRadiusDp.dp)
)
```

#### `Typography.kt` — fonte por brand:
```kotlin
// Banco Principal → FontFamily.Roboto (padrão Android)
// Fintech Verde   → carregarr via GoogleFonts("Inter") ou asset
// Banco Premium   → carregar via asset Georgia
val brandTypography = Typography(
    displayLarge = TextStyle(fontFamily = config.fontFamily, ...),
    ...
)
```

---

## iOS — Ajustes no SwiftUI Theme

### Arquivo: `iosApp/Theme/`

#### `BrandTheme.swift`:
```swift
struct BrandTheme {
    let primary: Color
    let secondary: Color
    let background: Color
    let surface: Color
    let onPrimary: Color
    let onBackground: Color
    let onSurface: Color
    let error: Color
    let fontFamily: String
    let cornerRadius: CGFloat
}
```

#### `BrandFonts.swift` — registrar fontes:
- Banco Principal: `UIFont.systemFont` (Roboto disponível no sistema, ou bundle)  
- Fintech Verde: adicionar `Inter-*.ttf` ao bundle iOS  
- Banco Premium: adicionar `Georgia-*.ttf` (já disponível no sistema iOS)  

#### Environment extension:
```swift
extension EnvironmentValues {
    var brandTheme: BrandTheme { ... }
}
```

---

## Checklist de Ajuste

- [ ] `WhiteLabelConfig.kt` com tokens atualizados para as 3 marcas  
- [ ] `Color.kt` reflete os 8 tokens de cor por marca  
- [ ] `Shape.kt` aplica `borderRadiusDp` da marca ativa  
- [ ] `Typography.kt` carrega a fonte correta da marca  
- [ ] `BrandTheme.swift` (iOS) com os mesmos 8 tokens  
- [ ] Fontes Inter e Georgia registradas no bundle iOS  
- [ ] Compose `MaterialTheme` wrapper recebe config da marca ativa  
- [ ] SwiftUI `EnvironmentObject` / `Environment` propagam `BrandTheme`  
