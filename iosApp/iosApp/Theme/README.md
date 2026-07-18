# Theme

Tema white-label SwiftUI consumindo `WhiteLabelConfig` do shared (SPEC-004 / SPEC-005).
Nenhuma cor hardcoded — tokens vêm de `ThemeTokens` via `BrandTheme`.

| Arquivo | Responsabilidade |
|---|---|
| `BrandTheme.swift` | 8 tokens de cor + `fontFamily` + `cornerRadius` + splash gradient/tracking (SPEC-006) |
| `BrandFonts.swift` | Roboto → system · Inter → bundle · Georgia → system (incl. Bold Italic) |
| `WhiteLabelEnvironment.swift` | `Environment` `whiteLabelConfig` + `brandTheme` |
| `ColorHex.swift` | `Color(hex:)` para mapear strings do shared |
| `Resources/Fonts/` | TTFs Inter (OFL) registrados via `UIAppFonts` |

Propagação em runtime: `iosAppApp` observa `AppStateRepository` e reaplica
`.environment(\.whiteLabelConfig, …)` + `.environment(\.brandTheme, …)`.
