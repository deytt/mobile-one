# SPEC-008 — Biometria Screen: Ajuste de Layout e Flavor

**Status:** Pronto para implementação  
**Tipo:** Layout / Brand  
**Figma:** [Banco Principal](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-6148) · [Fintech Verde](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-6315) · [Banco Premium](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-6473)

---

## Objetivo

Corrigir o layout da tela de Biometria para que reflita fielmente o design do Figma, com suporte completo aos 3 flavors de marca. Sem novas funcionalidades — apenas ajustes visuais e de espaçamento.

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  [StatusBar — bg colorPrimary]        │
├──────────────────────────────────────┤
│  [Header 64dp — BrandLogo centrado]   │
├──────────────────────────────────────┤
│  background: colorBackground          │
│  padding: top 40dp, horizontal 24dp   │
│                                       │
│         [Avatar 72dp circular]        │
│          gap 16dp                     │
│         "Olá, {nome}!"  22sp bold     │
│          gap 8dp                      │
│         "Confirme sua identidade..."  │
│          13sp, 220dp max width        │
│          gap 48dp                     │
│                                       │
│         [Biometric Button 96dp]       │
│          gap 12dp                     │
│         "Toque para usar biometria"   │
│          13sp medium colorPrimary     │
│                                       │
│  (flex expande — espaço flexível)     │
│                                       │
│         "Usar CPF e senha"            │  ← rodapé
│          13sp medium, underline       │
│          (padding bottom 32dp)        │
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Status Bar + Header
Igual ao Login (ver SPEC-007): status bar com `colorPrimary`, header 64dp com BrandLogo centralizado.

### Corpo (flex expande)
- Background: `colorBackground`
- Alinhamento horizontal: centralizado
- padding: top 40dp, bottom 32dp, horizontal 24dp

### Avatar do Usuário (72dp × 72dp)
- Shape: círculo (border-radius = 100%)
- Background: `colorPrimary`
- Texto: iniciais do usuário, 22sp bold, branco
- Font: da marca ativa
- Shadow: `drop-shadow(0px 4px 3px rgba(0,0,0,0.1), 0px 2px 2px rgba(0,0,0,0.1))`
- padding-bottom: 16dp

### Saudação
- "Olá, {nome}!"
- 22sp bold, `colorOnBackground`
- Alinhamento: centro
- letter-spacing: -0.44
- Banco Principal: Roboto Bold
- Fintech Verde: Inter Bold
- Banco Premium: Georgia Bold Italic
- padding-bottom: 8dp

### Subtítulo
- "Confirme sua identidade para acessar sua conta"
- 13sp regular, `colorOnSurface`
- Alinhamento: centro
- max-width: 220dp (quebra de linha)
- line-height: 17.875sp
- padding-bottom: 48dp

### Botão de Biometria (container 96dp × 96dp)
Estrutura em camadas:
1. **Halo** (72dp × 72dp, centralizado dentro do 96dp): círculo com `colorPrimary opacity 10%`
2. **Ícone** (52dp × 52dp): ícone de fingerprint, centralizado no container de 96dp (offset left: 22dp, top: 22dp)

- O ícone usa `colorPrimary` como tint
- padding-bottom abaixo: 12dp

### Label do Botão
- "Toque para usar biometria"
- 13sp medium, `colorPrimary`
- Alinhamento: centro

### Espaçador Flexível
- Ocupa todo o espaço restante entre o botão de biometria e o link de rodapé

### Link "Usar CPF e senha"
- 13sp medium, `colorOnSurface`
- Decoração: underline
- Alinhamento: centro
- padding-bottom: 32dp

---

## Diferenças por Marca (Flavor)

| Atributo | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| Header/StatusBar bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| Background | `#F5F7FA` | `#F0FAF5` | `#FAFAF8` |
| Avatar bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| Greeting font | Roboto Bold | Inter Bold | Georgia Bold Italic |
| Subtitle font | Roboto Regular | Inter Regular | Georgia Regular |
| Halo color | `#003B6F` 10% | `#00A86B` 10% | `#7B2D00` 10% |
| Biometric tint | `#003B6F` | `#00A86B` | `#7B2D00` |
| Link color | `#6B7280` | `#6B7280` | `#6B6B6B` |

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/auth/BiometricScreen.kt`

### Problemas a corrigir:
1. **Avatar**: usar `Canvas`/`Box` circular (não `Image`) com `colorPrimary` de fundo e texto das iniciais
2. **Halo biometria**: dois `Box` sobrepostos — halo 72dp circle opacity 10% + ícone 52dp
3. **Espaçador flexível**: `Spacer(modifier = Modifier.weight(1f))` entre botão e link
4. **Link underline**: `TextDecoration.Underline` no estilo do texto
5. **padding-bottom**: 48dp entre subtítulo e botão biometria (não usar hardcoded padding na tela antiga)

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    // StatusBar + Header (ver SPEC-007)
    
    // Body
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .shadow(elevation = 4.dp, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitials,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        Spacer(16.dp)
        
        Text(
            text = "Olá, $userName!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-0.44).sp
        )
        Spacer(8.dp)
        
        Text(
            text = "Confirme sua identidade para acessar sua conta",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 220.dp),
            lineHeight = 17.875.sp
        )
        Spacer(48.dp)
        
        // Biometric Button
        Box(
            modifier = Modifier
                .size(96.dp)
                .clickable { onBiometricTap() },
            contentAlignment = Alignment.Center
        ) {
            // Halo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            )
            // Icon
            Icon(
                painter = painterResource(R.drawable.ic_fingerprint),
                contentDescription = "Biometria",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(12.dp)
        
        Text(
            text = "Toque para usar biometria",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Link
        Text(
            text = "Usar CPF e senha",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onUseCpf() }
        )
    }
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/Auth/BiometricView.swift`

### Problemas a corrigir:
1. **Avatar**: `Circle()` com `brandTheme.primary` de fundo, texto das iniciais, `.shadow(radius: 4)`
2. **Halo + Ícone**: `ZStack` — `Circle` 72pt opacity 0.10 + `Image(systemName: "touchid")` 52pt
3. **Espaçador flexível**: `Spacer()` entre o botão e o link
4. **Link underline**: `.underline()` modifier

```swift
VStack(spacing: 0) {
    // StatusBar + Header (ver SPEC-007)
    
    // Body
    VStack(spacing: 0) {
        // Avatar
        ZStack {
            Circle()
                .fill(brandTheme.primary)
                .frame(width: 72, height: 72)
                .shadow(color: .black.opacity(0.1), radius: 3, x: 0, y: 4)
            
            Text(userInitials)
                .font(brandTheme.font(size: 22, weight: .bold))
                .foregroundColor(.white)
                .tracking(-0.44)
        }
        .padding(.bottom, 16)
        
        Text("Olá, \(userName)!")
            .font(brandTheme.font(size: 22, weight: .bold))
            .foregroundColor(brandTheme.onBackground)
            .tracking(-0.44)
            .padding(.bottom, 8)
        
        Text("Confirme sua identidade para acessar sua conta")
            .font(brandTheme.font(size: 13))
            .foregroundColor(brandTheme.onSurface)
            .multilineTextAlignment(.center)
            .frame(maxWidth: 220)
            .lineSpacing(4.875)
            .padding(.bottom, 48)
        
        // Biometric Button
        Button(action: onBiometricTap) {
            ZStack {
                Circle()
                    .fill(brandTheme.primary.opacity(0.10))
                    .frame(width: 72, height: 72)
                
                Image(systemName: "touchid")
                    .resizable()
                    .frame(width: 52, height: 52)
                    .foregroundColor(brandTheme.primary)
            }
            .frame(width: 96, height: 96)
        }
        .padding(.bottom, 12)
        
        Text("Toque para usar biometria")
            .font(brandTheme.font(size: 13, weight: .medium))
            .foregroundColor(brandTheme.primary)
        
        Spacer()
        
        Button("Usar CPF e senha") { onUseCpf() }
            .font(brandTheme.font(size: 13, weight: .medium))
            .foregroundColor(brandTheme.onSurface)
            .underline()
            .padding(.bottom, 32)
    }
    .padding(.horizontal, 24)
    .padding(.top, 40)
    .frame(maxHeight: .infinity)
    .background(brandTheme.background)
}
.navigationBarHidden(true)
```

---

## Checklist de Ajuste

- [ ] Header 64dp com `colorPrimary`, igual ao Login  
- [ ] `colorBackground` no corpo  
- [ ] Avatar 72dp circular, `colorPrimary` de fundo, sombra  
- [ ] Iniciais do usuário em branco, fonte da marca, 22sp bold  
- [ ] Saudação "Olá, {nome}!" 22sp bold, letter-spacing -0.44  
- [ ] Subtítulo 13sp, max-width 220dp, centralizado  
- [ ] Gap de 48dp antes do botão biometria  
- [ ] Halo 72dp círculo `colorPrimary opacity 10%` centralizado no container 96dp  
- [ ] Ícone fingerprint 52dp, tint `colorPrimary`  
- [ ] "Toque para usar biometria" 13sp medium `colorPrimary`  
- [ ] Espaçador flexível empurra link para rodapé  
- [ ] "Usar CPF e senha" 13sp medium `colorOnSurface` underline  
- [ ] padding-bottom 32dp no rodapé  
