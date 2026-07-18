# SPEC-006 — Splash Screen: Ajuste de Layout e Flavor

**Status:** Pronto para implementação  
**Tipo:** Layout / Brand  
**Figma:** [Banco Principal](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-5656) · [Fintech Verde](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-5806) · [Banco Premium](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-5947)

---

## Objetivo

Corrigir o layout da Splash Screen para que reflita fielmente o design do Figma, com suporte completo aos 3 flavors de marca. Não há novas funcionalidades — apenas ajustes visuais.

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  StatusBar (transparent, white icons) │
│                                       │
│                                       │
│                                       │
│            [BrandLogo 64dp]           │  ← centro vertical
│          "Nome da Marca" 22sp          │
│                                       │
│                                       │
│          • • •  (page dots)           │  ← rodapé
│   Seguro e regulado pelo...  10sp     │
│       (bottom padding 40dp)           │
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Background
- Gradiente radial centralizado horizontalmente e posicionado em ~40% do topo
- Gradiente: 3 stops, da cor primary mais clara para mais escura
- Banco Principal: `rgba(0,59,111,1)` → `rgba(0,45,87,1)` → `rgba(0,31,63,1)`
- Fintech Verde: `rgba(0,168,107,1)` → `rgba(0,138,88,1)` → `rgba(0,107,68,1)`
- Banco Premium: `rgba(123,45,0,1)` → `rgba(74,26,0,1)`

### Status Bar
- Transparente com ícones brancos (light content)
- Padding top: 12dp

### Área Central (flex expande)
- Alinhamento: centralizado horizontal e vertical
- padding horizontal: 32dp
- Gap entre logo e nome: 16dp

### BrandLogo (64dp × 64dp)
Veja SPEC-005 para especificação de cada marca. Dimensão: 64dp × 64dp.

| Marca | Radius |
|---|---|
| Banco Principal | 12dp |
| Fintech Verde | 32dp (circular) |
| Banco Premium | losango 45°, radius 4dp interno |

### Nome da Marca
- Tamanho: 22sp
- Cor: branco
- Alinhamento: centro
- Banco Principal: Roboto SemiBold, letter-spacing -0.22
- Fintech Verde: Inter SemiBold, letter-spacing -0.22
- Banco Premium: Georgia Bold Italic, letter-spacing +0.88

### Rodapé (shrink-0)
- padding-bottom: 40dp
- padding horizontal: 32dp
- Gap entre dots e texto: 12dp

#### Page Indicator Dots
- 3 pontos, tamanhos crescentes da esquerda para direita: ~4.8dp, ~5.1dp, ~6.4dp
- Cor: `rgba(255,255,255,0.4)`, opacidades: 31%, 40%, 76%
- Gap: 8dp entre pontos

#### Texto regulatório
- "Seguro e regulado pelo Banco Central do Brasil"
- 10sp, cor: `rgba(255,255,255,0.45)`
- Alinhamento: centro

---

## Diferenças por Marca (Flavor)

| Atributo | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| Background | Azul escuro radial | Verde radial | Marrom escuro radial |
| Logo shape | Rounded rect 12dp | Círculo | Losango rotacionado |
| Font | Roboto SemiBold | Inter SemiBold | Georgia Bold Italic |
| Letter spacing nome | -0.22 | -0.22 | +0.88 |

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/splash/SplashScreen.kt`

### Problemas a corrigir:
1. **Background**: substituir fundo sólido por `Brush.radialGradient` com os stops corretos por brand
2. **BrandLogo**: verificar se o shape está correto para cada marca (ver SPEC-005)
3. **Page dots**: implementar 3 bolinhas com tamanhos e opacidades variáveis
4. **Padding rodapé**: ajustar para 40dp fixo (não depende de safe area nativa)
5. **Typography**: aplicar fonte e letter-spacing da marca ativa

```kotlin
// Estrutura esperada:
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(brush = brandRadialGradient(config))
) {
    // Status bar area
    
    // Centro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BrandLogoLarge(config, size = 64.dp)
        Spacer(16.dp)
        Text(config.brandName, style = brandTextStyle(22.sp, fontWeight = SemiBold))
    }
    
    // Rodapé
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        PageDots(count = 3, activeIndex = 2)
        Spacer(12.dp)
        Text("Seguro e regulado...", fontSize = 10.sp, color = White.copy(alpha = 0.45f))
    }
}
```

### `brandRadialGradient()`:
```kotlin
fun brandRadialGradient(config: WhiteLabelConfig): Brush {
    return Brush.radialGradient(
        colorStops = config.splashGradientStops,
        center = Offset(/* center X, 40% height */)
    )
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/Splash/SplashView.swift`

### Problemas a corrigir:
1. **Background**: substituir cor sólida por `RadialGradient` com stops da marca
2. **Layout**: usar `GeometryReader` + `VStack` com `Spacer()` flexível
3. **BrandLogo**: aplicar tamanho 64×64, shape correto por marca
4. **Page dots**: três `Circle` com opacidades 0.31, 0.40, 0.76 e tamanhos 4.8, 5.1, 6.4
5. **Safe area**: `.ignoresSafeArea()` no background, padding de conteúdo manual

```swift
ZStack {
    // Background
    brandTheme.splashGradient
        .ignoresSafeArea()
    
    VStack(spacing: 0) {
        Spacer()
        
        // Logo + Name
        VStack(spacing: 16) {
            BrandLogoView(size: 64, theme: brandTheme)
            Text(brandTheme.brandName)
                .font(brandTheme.font(size: 22, weight: .semiBold))
                .foregroundColor(.white)
                .tracking(brandTheme.splashNameTracking)
        }
        
        Spacer()
        
        // Rodapé
        VStack(spacing: 12) {
            SplashPageDots()
            Text("Seguro e regulado pelo Banco Central do Brasil")
                .font(brandTheme.font(size: 10))
                .foregroundColor(.white.opacity(0.45))
                .multilineTextAlignment(.center)
        }
        .padding(.bottom, 40)
        .padding(.horizontal, 32)
    }
}
.statusBarStyle(.lightContent)
```

---

## Checklist de Ajuste

- [ ] Background radial gradient implementado (Android: `Brush.radialGradient`, iOS: `RadialGradient`)
- [ ] Gradiente com os stops corretos por marca  
- [ ] BrandLogo 64dp com shape correto por marca  
- [ ] Fonte e letter-spacing aplicados por marca  
- [ ] Page dots (3 bolinhas) com tamanho e opacidade crescentes  
- [ ] Texto regulatório 10sp, rgba(255,255,255,0.45)  
- [ ] Padding rodapé: 40dp bottom  
- [ ] Status bar: transparente, ícones brancos  

