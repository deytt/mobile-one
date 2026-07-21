# SPEC-010 — Home de Conta: Ajuste de Layout e Flavor

**Status:** Implementado
**Tipo:** Layout / Brand
**Referência visual:** consultar [`docs/figma/design-system.md`](../figma/design-system.md) após atualização com o Figma corporativo

---

## Objetivo

Padronizar o layout da Home de Conta conforme a referência visual corporativa, com suporte às configurações de marca. Compartilha o mesmo **Bottom Tab Bar** da Home de Cartões (ver SPEC-009).

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  StatusBar (colorPrimary)             │
├──────────────────────────────────────┤
│  [Header]                             │
│  Avatar  "Olá, {nome}"     [sino]      │
│  (igual ao Home Cartões)              │
├──────────────────────────────────────┤
│  [Card Saldo — bg colorPrimary]       │
│  Saldo disponível                     │
│  R$ 3.547,80  [eye icon]              │
│  [Pagar ↗]  [Extrato 📋]  [PIX ⊞]   │
├──────────────────────────────────────┤
│  [Card PIX]                           │
│  ⚡ PIX                               │
│  Transferências e pagamentos...      →│
├──────────────────────────────────────┤
│  [Card Open Finance]                  │
│  📊 Open Finance                      │
│  Conecte suas contas...             →│
├──────────────────────────────────────┤
│  Transações recentes    [Ver todas]   │
│  ┌─────────────────────────────────┐  │
│  │ [►] Assinatura     R$ 45,90     │  │
│  │ [🛒] Supermercado  R$ 234,50    │  │
│  │ [⚡] PIX recebido +R$ 500,00    │  │ verde
│  │ [💊] Farmácia      R$ 67,80     │  │
│  │ [🚗] Transporte    R$ 28,50     │  │
│  └─────────────────────────────────┘  │
├──────────────────────────────────────┤
│  [Cartões]  [Conta ●]     [⊞]        │ ← Bottom Bar
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Header
Idêntico ao Home de Cartões (ver SPEC-009). Background `colorPrimary`.

### Card de Saldo (Hero Card)
- Background: `colorPrimary`
- Transição suave para `colorBackground` logo abaixo (sem borda visível — usar shape + fundo da tela)
- padding: 20dp horizontal, 20dp vertical
- border-radius aplicado apenas nas bordas inferiores: `borderRadiusDp` (para criar efeito de card sobre fundo)

**Linha "Saldo disponível":**
- 13sp regular, branco opacity 70%

**Linha do valor:**
- Row: valor + ícone olho
- Valor: 28sp bold, branco, letter-spacing -0.56
- Ícone olho: 20dp, branco opacity 80%, gap 8dp da direita do valor

**Quick Actions (3 botões):**
- Row centralizado, gap 24dp, padding-top 20dp
- Cada action: Column (ícone circle + label)

**Ícone da action (48dp × 48dp):**
- Background: `rgba(255,255,255,0.15)`
- Shape: círculo
- Ícone interno: 20dp, branco

**Label da action:**
- 12sp medium, branco opacity 90%
- padding-top: 6dp

| Action | Ícone | Label |
|---|---|---|
| Pagar | seta diagonal ↗ | Pagar |
| Extrato | documento 📋 | Extrato |
| PIX | quadradinhos ⊞ | PIX |

### Cards de Feature (PIX / Open Finance)
- Background: `colorSurface` (#FFFFFF)
- border-radius: `borderRadiusDp`
- margin horizontal: 16dp, margin-top: 16dp
- padding: 16dp
- shadow: `0dp 1dp 3dp rgba(0,0,0,0.08)`

**Layout interno:**
- Row: ícone container + coluna de texto + spacer + chevron →

**Ícone container (40dp × 40dp):**
- Background: `colorBackground`
- Shape: círculo
- Ícone: 20dp, `colorPrimary`

**Texto:**
- Título: 15sp semibold, `colorOnBackground`
- Subtítulo: 12sp regular, `colorOnSurface`, max 2 linhas

**Chevron:**
- 16dp, `colorOnSurface`

### Section "Transações recentes"
- Header: padding horizontal 16dp, padding-top 20dp
  - "Transações recentes": 15sp semibold `colorOnBackground`
  - "Ver todas": 13sp medium `colorPrimary`
- Lista: card branco, border-radius 12dp, margin horizontal 16dp, margin-top 8dp

**Item de transação:**
- Ícone: 40dp círculo `colorBackground`, ícone de categoria 20dp `colorOnSurface`
- Nome: 14sp semibold `colorOnBackground`
- Data + categoria: 12sp `colorOnSurface`
- Valor saída: 14sp semibold `colorOnBackground`
- Valor entrada: 14sp semibold, `#22C55E` (verde, todas as marcas)
- Entrada PIX marcada como positiva: "+R$ 500,00"

### Bottom Tab Bar
Compartilhado com Home de Cartões. Na Home de Conta, a aba **"Conta"** está ativa (pill preenchida), e "Cartões" está inativa.

Ver SPEC-009 para a spec completa do componente `HomeTabBar`.

---

## Diferenças por Marca (Flavor)

| Atributo | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| Header/StatusBar | `#003B6F` | `#00A86B` | `#7B2D00` |
| Card saldo bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| Background | `#F5F7FA` | `#F0FAF5` | `#FAFAF8` |
| Border radius | 12dp | 16dp | 4dp |
| Tab ativo (Conta) | `#003B6F` | `#00A86B` | `#7B2D00` |
| Feature card ícone | `#003B6F` | `#00A86B` | `#7B2D00` |
| "Ver todas" | `#003B6F` | `#00A86B` | `#7B2D00` |

---

## Navegação entre Homes

A Home de Conta e a Home de Cartões são duas telas separadas, acessíveis via o Bottom Tab Switcher.

### Modelo de Navegação

O Bottom Tab Bar está presente em ambas as telas e controla a alternância. Não usar uma única tela com `when/if` — manter telas separadas com navegação real:

**Android:**
```kotlin
// MobileOneNavHost.kt
enum class HomeTab { CARTOES, CONTA }

// HomeScreen.kt usa NavController para navegar entre:
// Route.HomeCartoes ↔ Route.HomeConta
// A aba ativa é mantida em HomeViewModel via StateFlow<HomeTab>
```

**iOS:**
```swift
// HomeView.swift usa @State var currentTab: HomeTab
// Troca de aba: NavigationLink ou condicional no body
// Manter ambas as views no NavigationStack com TabView sem TabBar (custom bottom bar)
```

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/home/HomeContaScreen.kt`

### Pontos de implementação:
1. **Hero Card de saldo**: fundo `colorPrimary` deve se integrar visualmente com o header (zero gap entre header e card)
2. **Quick actions**: 3 botões circulares 48dp com background semitransparente, não usar ícones de system
3. **Feature cards**: shadow sutil, border-radius da marca, ícone `colorPrimary`
4. **Transação PIX entrada**: cor verde `#22C55E`, sinal de "+"
5. **Bottom bar**: `currentTab = HomeTab.CONTA` passado ao `HomeTabBar`

```kotlin
@Composable
fun HomeContaScreen(
    viewModel: HomeViewModel,
    onNavigateToCartoes: () -> Unit,
    onBrandSwitcher: () -> Unit,
    config: WhiteLabelConfig
) {
    Scaffold(
        bottomBar = {
            HomeTabBar(
                currentTab = HomeTab.CONTA,
                onTabChange = { if (it == HomeTab.CARTOES) onNavigateToCartoes() },
                onBrandSwitcher = onBrandSwitcher,
                config = config
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            // Header
            item { HomeHeader(config) }

            // Hero Card Saldo
            item {
                HeroCardSaldo(
                    saldo = viewModel.saldo,
                    isSaldoVisible = viewModel.isSaldoVisible,
                    onToggleVisibility = viewModel::toggleSaldoVisibility,
                    onPagar = { /* nav */ },
                    onExtrato = { /* nav */ },
                    onPix = { /* nav */ },
                    config = config
                )
            }

            // Feature Cards
            item { FeatureCard(type = FeatureType.PIX, config = config) }
            item { FeatureCard(type = FeatureType.OPEN_FINANCE, config = config) }

            // Transações
            item { SectionHeader("Transações recentes", onVerTodas = { /* nav */ }, config = config) }
            items(viewModel.transactions) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/Home/HomeContaView.swift`

### Pontos de implementação:
1. **Hero Card**: `ZStack` com `colorPrimary.ignoresSafeArea(edges: .top)` para continuidade visual com status bar
2. **Quick actions**: `HStack` com `Circle().fill(.white.opacity(0.15))` + ícones SF Symbols
3. **Saldo toggle**: `@State var isSaldoVisible` + ícone eye/eye.slash
4. **Transação entrada**: valor com prefixo "+" e cor `Color(hex: "22C55E")`

```swift
struct HomeContaView: View {
    @ObservedObject var viewModel: HomeViewModel
    @Environment(\.brandTheme) var brandTheme
    let onNavigateToCartoes: () -> Void
    let onBrandSwitcher: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Header + Hero Card com bg colorPrimary contíguo
            ZStack(alignment: .top) {
                brandTheme.primary
                    .frame(height: 220) // status + header + card

                VStack(spacing: 0) {
                    HomeHeaderView(theme: brandTheme, userName: viewModel.userName)
                    HeroCardSaldoView(viewModel: viewModel, theme: brandTheme)
                }
            }

            ScrollView {
                VStack(spacing: 16) {
                    FeatureCardView(type: .pix, theme: brandTheme)
                    FeatureCardView(type: .openFinance, theme: brandTheme)

                    SectionHeaderView(title: "Transações recentes",
                                      actionTitle: "Ver todas",
                                      theme: brandTheme)

                    TransactionListView(transactions: viewModel.transactions,
                                        theme: brandTheme)
                }
                .padding(.horizontal, 16)
            }

            HomeTabBar(
                currentTab: .constant(.conta),
                onTabChange: { if $0 == .cartoes { onNavigateToCartoes() } },
                onBrandSwitcher: onBrandSwitcher
            )
        }
        .ignoresSafeArea(edges: .top)
    }
}
```

---

## Checklist de Ajuste

- [x] Status bar `colorPrimary`, header `colorPrimary` contínuos (sem separação)
- [x] "Saldo disponível" 13sp branco opacity 70%
- [x] Valor 28sp bold branco, ícone olho 20dp
- [x] 3 quick actions circulares 48dp, fundo `rgba(255,255,255,0.15)`
- [x] Labels das actions 12sp medium branco
- [x] Feature cards com shadow, border-radius da marca, ícone `colorPrimary`
- [x] Transação entrada com valor verde `#22C55E` e sinal "+"
- [x] "Ver todas" 13sp `colorPrimary`
- [ ] **NOVO** Bottom Bar com "Conta" ativa (pill preenchida `colorPrimary`)
- [ ] "Cartões" inativo navega para HomeCartoesScreen
- [ ] Botão grade abre BrandSwitcher
- [ ] Bottom bar respeita safe area inferior
