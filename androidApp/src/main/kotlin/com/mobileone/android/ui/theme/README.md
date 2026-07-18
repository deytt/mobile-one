# ui/theme

`BankTheme` consumindo `WhiteLabelConfig` do shared (SPEC-004 / SPEC-005). Nenhuma cor
hardcoded — sempre via `MaterialTheme.colorScheme` / `shapes` / `typography`.

| Arquivo | Responsabilidade |
|---|---|
| `Color.kt` | 8 tokens de cor → `ColorScheme` |
| `Shape.kt` | `borderRadiusDp` → `Shapes` (small/medium/large) |
| `Typography.kt` | Roboto / Inter / Georgia (Gelasio) → `Typography` |
| `SplashGradient.kt` | Gradiente radial da Splash por marca (SPEC-006) |
| `LocalWhiteLabelConfig.kt` | CompositionLocal da marca ativa |
| `BankTheme.kt` | Wrapper `MaterialTheme` + CompositionLocal |
