# SPEC-011 — Brand Switcher Interno: Layout

**Status:** Implementado
**Tipo:** Layout / Feature
**Referência visual:** consultar [`docs/figma/design-system.md`](../figma/design-system.md) após atualização com o Figma corporativo

---

## Objetivo

Padronizar o layout da tela interna Brand Switcher conforme a referência visual corporativa. Esta tela é acessada pelo botão grade no rodapé das Homes em builds internos e permite alternar entre as configurações de marca em tempo de execução.

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  StatusBar (bg #1A1A2E, ícones brancos│
├──────────────────────────────────────┤
│  [Header — bg #1A1A2E]               │
│  "Brand Switcher"  18sp bold         │
│  [INTERNO]  ← badge laranja          │
│  "Validação interna"  12sp         │
├──────────────────────────────────────┤
│  bg: #F4F4F6                         │
│  padding 20dp top, 16dp horizontal   │
│                                       │
│  SELECIONAR MARCA  (label seção)      │
│                                       │
│  ┌──────────────────────────────────┐ │
│  │ ◉  [■ BP]  Banco Principal       │ │ ← selecionado
│  │            Roboto · 12px radius  │ │
│  │            [#003B6F] [#F7941D]   │ │
│  │                                ✓│ │
│  └──────────────────────────────────┘ │
│                                       │
│  ┌──────────────────────────────────┐ │
│  │ ○  [● FV]  Fintech Verde         │ │
│  │            Inter · 16px radius   │ │
│  │            [#00A86B] [#1A1A2E]   │ │
│  └──────────────────────────────────┘ │
│                                       │
│  ┌──────────────────────────────────┐ │
│  │ ○  [◆ BP]  Banco Premium         │ │
│  │            Georgia · 4px radius  │ │
│  │            [#7B2D00] [#C9A84C]   │ │
│  └──────────────────────────────────┘ │
│                                       │
│  (flex expande)                       │
│                                       │
│  [Aplicar marca] ← botão filled       │
│  "As mudanças são aplicadas..."       │
│  (padding bottom 32dp)                │
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Status Bar
- Background: `#1A1A2E` (escuro fixo, independente da marca ativa)
- Ícones: brancos

### Header
- Background: `#1A1A2E`
- padding: top 16dp, bottom 20dp, horizontal 16dp
- Posicionamento relativo: badge "INTERNO" fica posicionado no canto superior direito do header

**Título "Brand Switcher":**
- 18sp bold, Inter Bold, branco
- line-height: 27dp

**Badge "INTERNO":**
- Background: `#F7941D` (sempre laranja, independente da marca)
- border-radius: 4dp
- padding: horizontal 8dp, vertical 2dp
- Texto: 10sp Inter Bold, branco, letter-spacing 0.6
- Posição: absoluta, alinhado ao topo direito do header (top: 16dp, right: 16dp)

**Subtítulo "Validação interna":**
- 12sp Inter Regular, `rgba(255,255,255,0.45)`

### Corpo
- Background: `#F4F4F6` (cinza claro fixo)
- padding: top 20dp, horizontal 16dp

**Label de seção "SELECIONAR MARCA":**
- 11sp Inter SemiBold, `#6B7280`, letter-spacing 0.77, UPPERCASE
- padding-bottom: 12dp (espaço antes dos cards)
- Texto: "Selecionar marca" (UPPERCASE via transformação, não hardcoded)

### Cards de Marca

Cada card representa uma marca selecionável.

**Card não selecionado:**
- Background: `#FFFFFF`
- border: 2dp `rgba(0,0,0,0)` (invisível)
- shadow: `0dp 1dp 1.5dp rgba(0,0,0,0.08)`
- border-radius: 12dp (fixo para todos os cards, independente da marca do item)

**Card selecionado:**
- Background: `#FFFFFF`
- border: 2dp `colorPrimary` da marca selecionada
- shadow: `0dp 0dp 0px rgba(colorPrimary, 0.13)`
- border-radius: 12dp

**Layout interno do card (padding: 16dp):**
```
Row(verticalAlignment = Center) {
    RadioButton(20dp)     // estado selecionado/não
    gap: 12dp
    BrandAvatar(24dp)     // quadrado/shape da marca
    gap: 12dp
    Column {              // flex-1
        BrandName()       // nome com fonte da marca
        BrandDescription()// fonte · radius
        BrandColorPills() // pills de cores
    }
    if (selected) CheckIcon(16dp)
}
```

**Radio Button (20dp):**
- Não selecionado: borda 2dp `#D1D5DB`, sem preenchimento
- Selecionado: borda 2dp `colorPrimary` da marca, fill `colorPrimary`, ponto branco 8dp interno

**Brand Avatar (24dp × 24dp):**
- Cada marca usa o shape e cor primária definidos em SPEC-005:
  - Banco Principal: `#003B6F`, radius 12dp (rounded rect)
  - Fintech Verde: `#00A86B`, radius 16dp (quase círculo)
  - Banco Premium: `#7B2D00`, radius 4dp (sharp rect)

**Nome da Marca:**
- 14sp bold, `#1A1A2E`
- **Fonte específica da marca do item** (não da marca ativa):
  - Banco Principal: Roboto Bold
  - Fintech Verde: Inter Bold
  - Banco Premium: Georgia Bold

**Descrição:**
- 11sp Inter Medium, `#6B7280`
- Conteúdo: "{fonte} · {radius}px radius"

**Pills de Cores:**
Row com pills (altura 19dp):
- Cada pill: padding horizontal 8dp, vertical 2dp, border-radius 100dp
- Conteúdo: dot 8dp + código hex 10sp Inter SemiBold
- Cor do dot e texto = a cor representada
- Background: `rgba(cor, 0.09)`
- Duas pills: primary e secondary da marca do item

**Ícone de Check:**
- 16dp, `colorPrimary` da marca selecionada
- Só visível quando o card está selecionado

### Botão "Aplicar marca"
- height: 52dp, width: full (padding horizontal 16dp)
- background: `colorPrimary` **da marca selecionada** (não necessariamente a ativa)
- border-radius: **12dp** (fixo — não usa o `borderRadiusDp` da marca)
- Texto: 15sp Inter Bold, branco
- Ação: aplica o flavor selecionado e fecha/volta

### Nota de Rodapé
- "As mudanças são aplicadas instantaneamente em todo o app"
- 11sp Inter Regular, `#9CA3AF`
- Alinhamento: centro, max-width do botão
- padding-top: 12dp abaixo do botão

---

## Comportamento

1. Tela abre com a marca **atualmente ativa** já selecionada
2. Tocar em outro card seleciona visualmente sem aplicar ainda
3. Botão "Aplicar marca" confirma a troca
4. Ao confirmar: `WhiteLabelConfig` é atualizado → toda a UI re-renderiza com o novo tema
5. Sem animação de transição complexa: rebuild simples da árvore de componentes

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/brandswitcher/BrandSwitcherScreen.kt`

```kotlin
@Composable
fun BrandSwitcherScreen(
    viewModel: BrandSwitcherViewModel,
    onBack: () -> Unit
) {
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val activeBrand by viewModel.activeBrand.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header (bg #1A1A2E)
        BrandSwitcherHeader()

        // Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F6))
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(20.dp)

            // Section label
            Text(
                "Selecionar marca".uppercase(),
                style = TextStyle(
                    fontFamily = InterFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.77.sp
                )
            )
            Spacer(12.dp)

            // Cards
            BrandCard.entries.forEach { brand ->
                BrandOptionCard(
                    brand = brand,
                    isSelected = brand == selectedBrand,
                    onSelect = { viewModel.selectBrand(brand) }
                )
                Spacer(12.dp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão
            Button(
                onClick = { viewModel.applyBrand(); onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedBrand.colorPrimary
                )
            ) {
                Text("Aplicar marca", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                     fontFamily = InterFont)
            }

            Text(
                "As mudanças são aplicadas instantaneamente em todo o app",
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                fontFamily = InterFont,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            Spacer(32.dp)
        }
    }
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/BrandSwitcher/BrandSwitcherView.swift`

```swift
struct BrandSwitcherView: View {
    @StateObject var viewModel = BrandSwitcherViewModel()
    @Environment(\.dismiss) var dismiss

    var body: some View {
        VStack(spacing: 0) {
            // Header
            BrandSwitcherHeader()

            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Selecionar marca".uppercased())
                        .font(.custom("Inter-SemiBold", size: 11))
                        .foregroundColor(Color(hex: "6B7280"))
                        .tracking(0.77)
                        .padding(.top, 20)

                    ForEach(BrandConfig.allBrands) { brand in
                        BrandOptionCard(
                            brand: brand,
                            isSelected: viewModel.selectedBrand.id == brand.id,
                            onSelect: { viewModel.selectedBrand = brand }
                        )
                    }

                    Spacer(minLength: 24)

                    Button(action: {
                        viewModel.applyBrand()
                        dismiss()
                    }) {
                        Text("Aplicar marca")
                            .font(.custom("Inter-Bold", size: 15))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                    }
                    .background(viewModel.selectedBrand.primary)
                    .cornerRadius(12)

                    Text("As mudanças são aplicadas instantaneamente em todo o app")
                        .font(.custom("Inter-Regular", size: 11))
                        .foregroundColor(Color(hex: "9CA3AF"))
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                        .padding(.top, 12)
                        .padding(.bottom, 32)
                }
                .padding(.horizontal, 16)
            }
            .background(Color(hex: "F4F4F6"))
        }
        .navigationBarHidden(true)
    }
}
```

---

## Checklist de Ajuste

- [x] Status bar `#1A1A2E`, ícones brancos (independente da marca ativa)
- [x] Header `#1A1A2E`, título 18sp Inter Bold branco
- [x] Badge "INTERNO" laranja `#F7941D`, posição absoluta canto superior direito
- [x] Subtítulo "Validação interna" 12sp `rgba(255,255,255,0.45)`
- [x] Background do corpo: `#F4F4F6`
- [x] Label seção "SELECIONAR MARCA" 11sp uppercase, letter-spacing 0.77
- [x] Cards com border 2dp: invisível (não selecionado) / `colorPrimary` (selecionado)
- [x] Radio button: borda `#D1D5DB` vs preenchido `colorPrimary`
- [x] Brand Avatar 24dp com shape e cor da marca do item
- [x] Nome da marca com **fonte da marca do item** (não da ativa)
- [x] Descrição 11sp Inter: "{fonte} · {radius}px radius"
- [x] Pills de cores com dot + hex, background `rgba(cor, 0.09)`
- [x] Check icon 16dp visível apenas no selecionado
- [x] Botão "Aplicar marca" com `colorPrimary` da marca **selecionada**
- [x] border-radius do botão: 12dp (fixo)
- [x] Nota de rodapé 11sp `#9CA3AF`, centralizado
- [ ] Ao aplicar: `WhiteLabelConfig` atualizado → toda UI re-renderiza
