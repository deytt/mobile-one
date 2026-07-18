# SPEC-009 — Home de Cartões: Ajuste de Layout e Flavor

**Status:** Pronto para implementação  
**Tipo:** Layout / Brand  
**Figma:** [Banco Principal](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-2769) · [Fintech Verde](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-3081) · [Banco Premium](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-3400)

---

## Objetivo

Corrigir o layout da Home de Cartões para que reflita fielmente o design do Figma, com suporte aos 3 flavors. Inclui o novo **Bottom Tab Switcher** com navegação entre Home Cartões e Home Conta, e botão de Brand Switcher no canto inferior direito.

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  StatusBar (colorPrimary)             │
├──────────────────────────────────────┤
│  [Header]                             │
│  Avatar  "Olá, Heitor"    [sino]      │
│  padding: 16dp                        │
├──────────────────────────────────────┤
│  [Card Fatura — bg colorPrimary]      │
│  "Fatura aberta"                      │
│  R$ 487,40  (grande)                  │
│  Vencimento 25 JUL · Melhor dia 20 JUL│
│  chevron →                            │
├──────────────────────────────────────┤
│  [Pagar fatura]   [Meus cartões]      │
│  (2 botões em linha, gap 12dp)        │
├──────────────────────────────────────┤
│  ─── Meu limite ─── (→ chevron)      │
│  Utilizado R$750  Disponível R$2000   │
│  Barra de progresso                   │
│  Limite total: R$ 2.750,00            │
├──────────────────────────────────────┤
│  Minhas compras          [Ver todas]  │
│  ┌─────────────────────────────────┐  │
│  │ [ícone] Amazon   R$ 189,90      │  │
│  │         15 Jul · Online [1/3]   │  │
│  │ [ícone] iFood    R$ 67,50       │  │
│  │ [ícone] Posto Shell  R$ 150,00  │  │
│  │ [ícone] Cinemark R$ 80,00       │  │
│  └─────────────────────────────────┘  │
├──────────────────────────────────────┤
│  [Cartões ●]  [Conta]     [⊞]        │ ← Bottom Bar
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Status Bar
- Background: `colorPrimary`
- Ícones: branco

### Header
- Background: `colorPrimary`
- padding: 16dp todos os lados
- Linha: Avatar (40dp) + "Olá, {nome}" + Spacer + ícone sino (24dp)
- Avatar: círculo `colorPrimary`, borda branca 2dp, iniciais em branco 15sp bold
- "Olá, {nome}": 18sp bold, branco
- Sino: ícone outline branco 24dp

### Card Fatura
- Background: `colorPrimary` (shade levemente mais escuro — rgba white 10% overlay)
- border-radius: `borderRadiusDp`
- margin horizontal: 16dp, margin-top: 0 (integrado com header), padding: 16dp
- Layout:
  - "Fatura aberta": 12sp, branco opacity 70%, letter-spacing 0.3
  - Valor: 28sp bold, branco, letter-spacing -0.56
  - "Vencimento **25 JUL**": 13sp, branco opacity 80%; data em bold
  - "Melhor dia de compra **20 JUL**": 13sp, branco opacity 80%; data em bold
  - Chevron →: 16dp, branco opacity 60%, alinhado à direita

### Botões de Ação (abaixo do card, bg: `colorBackground`)
- Disposição: Row, gap 12dp, padding horizontal 16dp, padding-top 16dp
- Cada botão: height 44dp, flex=1, border-radius: `borderRadiusDp`

**"Pagar fatura"** (filled):
- background: `colorPrimary`
- ícone: 18dp branco (cartão/pagamento)
- texto: 14sp semibold, branco

**"Meus cartões"** (outlined):
- background: transparente
- border: 1dp `colorPrimary`
- ícone: 18dp `colorPrimary`
- texto: 14sp semibold, `colorPrimary`

### Section "Meu limite"
- Container: card branco, padding 16dp, border-radius 12dp, margin horizontal 16dp, margin-top 16dp
- Linha superior: "Meu limite" (15sp semibold `colorOnBackground`) + chevron 16dp `colorOnSurface`
- Linha valores: "Utilizado" + valor (14sp bold) à esquerda, "Disponível" + valor (14sp bold) à direita
- Barra de progresso: height 4dp, border-radius 2dp, track: `colorOnSurface opacity 20%`, fill: `colorPrimary`
- "Limite total: R$ X.XXX,00": 12sp `colorOnSurface`, alinhado à direita

### Section "Minhas compras"
- Header da section: "Minhas compras" (15sp semibold) + "Ver todas" (13sp `colorPrimary`)
- padding-top: 20dp, padding horizontal: 16dp
- Lista de transações: card branco, border-radius 12dp, sem gap entre itens (divididos por linha 1dp `colorBackground`)

**Item de transação:**
- Ícone: 40dp círculo `colorBackground`, ícone de categoria 20dp `colorOnSurface`
- Nome: 14sp semibold `colorOnBackground`
- Data + categoria: 12sp `colorOnSurface`
- Valor: 14sp semibold `colorOnBackground`, alinhado à direita
- Badge (ex: "1/3"): 14dp altura, background `colorSecondary`, texto 10sp bold branco

### Bottom Tab Bar (NOVO)
- Background: `colorSurface` (#FFFFFF)
- Shadow: `0dp -1dp 8dp rgba(0,0,0,0.08)`
- height: 72dp + safe area inferior
- padding horizontal: 16dp, padding-top: 12dp
- Layout: `Row`, `Spacer` entre switcher e botão grade

**Tab Switcher (esquerda):**
- Container: `colorBackground` (#F5F7FA), border-radius 100dp (pílula), padding 4dp
- Aba ativa ("Cartões"): pill preenchida `colorPrimary`, texto 14sp semibold branco
- Aba inativa ("Conta"): sem preenchimento, texto 14sp medium `colorOnSurface`
- Animação: `animateColorAsState` (Compose) / `withAnimation` (SwiftUI) na transição

**Botão Grade (direita — abre Brand Switcher):**
- Tamanho: 44dp × 44dp
- Shape: círculo
- Background: `colorPrimary`
- Ícone: 20dp, quadradinhos (grid 2×2), branco
- Ação: navega para BrandSwitcher

---

## Diferenças por Marca (Flavor)

| Atributo | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| Header/StatusBar | `#003B6F` | `#00A86B` | `#7B2D00` |
| Card fatura bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| Background | `#F5F7FA` | `#F0FAF5` | `#FAFAF8` |
| Border radius | 12dp | 16dp | 4dp |
| Tab ativo bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| "Ver todas" cor | `#003B6F` | `#00A86B` | `#7B2D00` |
| Badge cor | `#F7941D` | `#1A1A2E` | `#C9A84C` |
| Barra progresso | `#003B6F` | `#00A86B` | `#7B2D00` |

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/home/HomeScreen.kt` + `androidApp/ui/screen/home/components/`

### Problemas a corrigir:
1. **Header bg**: usar `colorPrimary` consistente com StatusBar
2. **Card fatura**: aplicar `borderRadiusDp` da marca, não hardcoded 12
3. **Bottom Bar**: implementar novo componente `HomeTabBar` com:
   - `TabSwitcher` (Cartões / Conta) como pílula animada
   - `BrandSwitcherButton` (círculo colorPrimary, ícone grid)
4. **Botões de ação**: garantir flex-1 para os dois botões terem largura igual
5. **Barra de progresso**: usar `LinearProgressIndicator` com `colorPrimary`

```kotlin
// HomeTabBar.kt
@Composable
fun HomeTabBar(
    currentTab: HomeTab,
    onTabChange: (HomeTab) -> Unit,
    onBrandSwitcher: () -> Unit,
    config: WhiteLabelConfig
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab Switcher (pílula)
            TabSwitcher(
                tabs = listOf(HomeTab.CARTOES, HomeTab.CONTA),
                activeTab = currentTab,
                onTabChange = onTabChange,
                config = config,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(16.dp)
            
            // Botão Brand Switcher
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onBrandSwitcher() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_grid),
                    contentDescription = "Brand Switcher",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/Home/HomeView.swift` (Home de Cartões) + `iosApp/Views/Home/Components/HomeTabBar.swift`

### Problemas a corrigir:
1. **Header**: aplicar `brandTheme.primary` no ZStack sobre a safe area
2. **Card fatura**: `cornerRadius(brandTheme.cornerRadius)`, sem hardcoded
3. **Bottom Bar**: novo `HomeTabBar` com pílula animada e botão grade
4. **Barra progresso**: `GeometryReader` para calcular width, fill `brandTheme.primary`

```swift
// HomeTabBar.swift
struct HomeTabBar: View {
    @Binding var currentTab: HomeTab
    let onBrandSwitcher: () -> Void
    @Environment(\.brandTheme) var brandTheme
    
    var body: some View {
        HStack(spacing: 16) {
            // Tab Switcher
            HStack(spacing: 4) {
                ForEach(HomeTab.allCases) { tab in
                    Button(action: { withAnimation(.easeInOut(duration: 0.2)) { currentTab = tab } }) {
                        Text(tab.title)
                            .font(brandTheme.font(size: 14, weight: currentTab == tab ? .semiBold : .medium))
                            .foregroundColor(currentTab == tab ? .white : brandTheme.onSurface)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(
                                currentTab == tab
                                    ? brandTheme.primary
                                    : Color.clear
                            )
                            .clipShape(Capsule())
                    }
                }
            }
            .padding(4)
            .background(brandTheme.background)
            .clipShape(Capsule())
            
            Spacer()
            
            // Brand Switcher Button
            Button(action: onBrandSwitcher) {
                Image(systemName: "grid.2x2")
                    .resizable()
                    .frame(width: 20, height: 20)
                    .foregroundColor(.white)
                    .frame(width: 44, height: 44)
                    .background(brandTheme.primary)
                    .clipShape(Circle())
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 12)
        .padding(.bottom, 8)
        .background(Color.white)
        .shadow(color: .black.opacity(0.08), radius: 8, x: 0, y: -1)
    }
}
```

---

## Checklist de Ajuste

- [x] Status bar `colorPrimary`, ícones brancos  
- [x] Header `colorPrimary`, avatar 40dp, sino 24dp  
- [x] Card fatura com `borderRadiusDp` da marca  
- [x] "Fatura aberta" 12sp, valor 28sp bold  
- [x] Botões "Pagar fatura" e "Meus cartões" flex-1, altura 44dp  
- [x] Section "Meu limite" com barra de progresso `colorPrimary`  
- [x] Lista de compras com ícones 40dp e badge `colorSecondary`  
- [x] **NOVO** Bottom Bar com pílula switcher Cartões/Conta  
- [x] Aba ativa pill preenchida `colorPrimary`, inativa sem fill  
- [x] Animação na troca de aba  
- [x] **NOVO** Botão grade 44dp círculo `colorPrimary` no canto direito  
- [x] Botão grade navega para BrandSwitcher  
- [x] Bottom bar respeita safe area inferior  
