# SPEC-007 — Login Screen: Ajuste de Layout e Flavor

**Status:** Pronto para implementação  
**Tipo:** Layout / Brand  
**Figma:** [Banco Principal](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-4955) · [Fintech Verde](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-5162) · [Banco Premium](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=34-5375)

---

## Objetivo

Corrigir o layout da tela de Login para que reflita fielmente o design do Figma, com suporte completo aos 3 flavors. Sem novas funcionalidades — apenas ajustes visuais e de espaçamento.

---

## Layout Geral

```
┌──────────────────────────────────────┐
│  [StatusBar — bg colorPrimary]        │
├──────────────────────────────────────┤
│  [Header 64dp — BrandLogo centrado]   │
├──────────────────────────────────────┤
│  padding 32dp top, 24dp horizontal    │
│                                       │
│  "Bem-vindo de volta"  24sp bold      │
│  "Entre com seu CPF e senha"  13sp    │
│  (gap 24dp abaixo)                    │
│                                       │
│  CPF  (label 12sp semibold)           │
│  ┌─────────────────────────────────┐  │
│  │ 000.000.000-00      [mask icon] │  │ 48dp height
│  └─────────────────────────────────┘  │
│                                       │
│  Senha  (label 12sp semibold)         │
│  ┌─────────────────────────────────┐  │
│  │ ••••••              [eye icon]  │  │ 48dp height
│  └─────────────────────────────────┘  │
│               Esqueci minha senha →   │ (gap 32dp abaixo)
│                                       │
│  [Entrar] (52dp, filled, disabled)    │ (gap 12dp)
│  [Entrar com biometria] (52dp,outline)│ (gap 24dp)
│                                       │
│  ─────────── ou ───────────           │
│                                       │
│  Ainda não tem conta? Abra a sua grátis│
│  (padding bottom 24dp)                │
└──────────────────────────────────────┘
```

---

## Especificações de Layout

### Status Bar
- Background: `colorPrimary`
- Ícones: branco
- Padding top: 12dp

### Header (height: 64dp)
- Background: `colorPrimary`
- Conteúdo: `BrandLogo` centralizado horizontal e verticalmente
- BrandLogo: versão pequena — container 28dp, texto 11sp, nome 13sp

### Corpo (flex expande)
- Background: `colorBackground`
- padding: top 32dp, bottom 24dp, horizontal 24dp

### Título
- "Bem-vindo de volta"
- 24sp, bold, `colorOnBackground`
- letter-spacing: -0.48 (todos)
- Banco Principal: Roboto Bold
- Fintech Verde: Inter Bold
- Banco Premium: Georgia Bold Italic

### Subtítulo
- "Entre com seu CPF e senha"
- 13sp, regular, `colorOnSurface`
- padding-bottom: 24dp

### Labels de Campo
- 12sp, semibold, `colorOnBackground`
- padding-bottom: 4dp

### Campos de Input (Text Field)
- height: 48dp
- background: `colorSurface` (#FFFFFF)
- border: 1dp, `rgba(107,114,128,0.3)`
- border-radius: `borderRadiusDp` (12/16/4dp)
- padding interno: left 15dp, right 45dp
- placeholder: 14sp, `rgba(colorOnBackground, 0.5)`
- Ícone (mask/eye): 16×16dp, posicionado a 15dp da borda direita

### Link "Esqueci minha senha"
- 12sp, medium, `colorPrimary`
- Alinhamento: direita
- Container de 50dp de height (com padding-bottom de 32dp para espaçamento abaixo)

### Botão Primário "Entrar"
- height: 52dp, width: full (com padding horizontal 24dp = 278dp útil)
- background: `colorPrimary` (quando habilitado) / `#6B7280 opacity 30%` (quando desabilitado)
- border-radius: `borderRadiusDp`
- Texto: 15sp bold, branco, centralizado
- padding-bottom após botão: 12dp

### Botão Secundário "Entrar com biometria"
- height: 52dp
- background: transparente
- border: 1dp `colorPrimary`
- border-radius: `borderRadiusDp`
- Ícone fingerprint: 18×18dp
- Texto: 15sp semibold, `colorPrimary`
- Gap ícone-texto: 8dp
- padding-bottom após botão: 24dp

### Divisor "ou"
- Linha esquerda + texto + linha direita
- Linhas: 1dp, `rgba(107,114,128,0.25)`
- Texto: 12sp, regular, `colorOnSurface`, opacity 60%
- Container height: 38dp (inclui padding-bottom 20dp)

### Link de cadastro
- "Ainda não tem conta?" 13sp, regular, `colorOnSurface`
- "Abra a sua grátis" 13sp, semibold, `colorPrimary`
- Inline, centralizado

---

## Diferenças por Marca (Flavor)

| Atributo | Banco Principal | Fintech Verde | Banco Premium |
|---|---|---|---|
| Header bg | `#003B6F` | `#00A86B` | `#7B2D00` |
| Background | `#F5F7FA` | `#F0FAF5` | `#FAFAF8` |
| Título font | Roboto Bold | Inter Bold | Georgia Bold Italic |
| Labels font | Roboto SemiBold | Inter SemiBold | Georgia SemiBold |
| Input radius | 12dp | 16dp | 4dp |
| Botão radius | 12dp | 16dp | 4dp |
| "Esqueci" cor | `#003B6F` | `#00A86B` | `#7B2D00` |
| On-surface | `#6B7280` | `#6B7280` | `#6B6B6B` |
| On-bg | `#1A1A2E` | `#1A1A2E` | `#1A1A1A` |

---

## Android — Compose

**Arquivo:** `androidApp/ui/screen/auth/LoginScreen.kt`

### Problemas a corrigir:
1. **Header**: verificar que tem exatamente 64dp de height e usa `colorPrimary` de fundo
2. **Input fields**: raio dos cantos deve respeitar `borderRadiusDp` da marca
3. **Botão primário**: estado desabilitado com cor cinza + opacidade 30%
4. **Botão biometria**: border deve ser exatamente 1dp (não usar `OutlinedButton` padrão que pode ter espessura diferente)
5. **Espaçamentos**: gap de 24dp depois do subtítulo, 32dp depois do password/link
6. **Fonte**: aplicar `MaterialTheme.typography` que já reflete a fonte da marca

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
) {
    // Header
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        BrandLogoSmall(config)
    }
    
    // Body
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Bem-vindo de volta", style = MaterialTheme.typography.headlineMedium)
        Spacer(4.dp)
        Text("Entre com seu CPF e senha", style = MaterialTheme.typography.bodyMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(24.dp)
        
        // CPF Field
        FieldLabel("CPF")
        BrandTextField(value = cpf, placeholder = "000.000.000-00", trailingIcon = MaskIcon)
        Spacer(16.dp)
        
        // Password Field
        FieldLabel("Senha")
        BrandTextField(value = password, placeholder = "••••••",
                       trailingIcon = if (showPass) EyeOffIcon else EyeIcon,
                       visualTransformation = if (showPass) Visual.None else PasswordVisual)
        
        // "Esqueci" link
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onForgotPassword) {
                Text("Esqueci minha senha", color = MaterialTheme.colorScheme.primary,
                     fontSize = 12.sp)
            }
        }
        Spacer(8.dp)
        
        // Entrar
        Button(
            onClick = onLogin,
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(config.borderRadiusDp.dp)
        ) {
            Text("Entrar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(12.dp)
        
        // Biometria
        OutlinedButton(
            onClick = onBiometric,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(config.borderRadiusDp.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(FingerprintIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(8.dp)
            Text("Entrar com biometria", color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
        }
        Spacer(24.dp)
        
        // Divisor
        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(Modifier.weight(1f), color = Color(0xFF6B7280).copy(alpha = 0.25f))
            Text("  ou  ", color = Color(0xFF6B7280).copy(alpha = 0.6f), fontSize = 12.sp)
            Divider(Modifier.weight(1f), color = Color(0xFF6B7280).copy(alpha = 0.25f))
        }
        Spacer(18.dp)
        
        // Cadastro link
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Ainda não tem conta? ", color = colorOnSurface, fontSize = 13.sp)
            ClickableText("Abra a sua grátis", color = colorPrimary, fontSize = 13.sp)
        }
    }
}
```

---

## iOS — SwiftUI

**Arquivo:** `iosApp/Views/Auth/LoginView.swift`

### Problemas a corrigir:
1. **Header**: height 64pt com `colorPrimary` de fundo; remover NavigationBar padrão
2. **Input radius**: usar `brandTheme.cornerRadius` nos `RoundedRectangle`
3. **Botão primário**: estado `.disabled` com opacidade 30% e cor cinza
4. **Botão biometria**: `overlay(RoundedRectangle.stroke(colorPrimary, 1))`
5. **Espaçamentos**: seguir os valores do spec acima

```swift
VStack(spacing: 0) {
    // Header
    ZStack {
        brandTheme.primary.ignoresSafeArea(edges: .top)
        BrandLogoSmall(theme: brandTheme)
    }
    .frame(height: 64)
    
    // Body
    ScrollView {
        VStack(alignment: .leading, spacing: 0) {
            Text("Bem-vindo de volta")
                .font(brandTheme.font(size: 24, weight: .bold))
                .foregroundColor(brandTheme.onBackground)
                .padding(.top, 32)
            
            Text("Entre com seu CPF e senha")
                .font(brandTheme.font(size: 13))
                .foregroundColor(brandTheme.onSurface)
                .padding(.top, 4)
                .padding(.bottom, 24)
            
            FieldLabel("CPF")
            BrandTextField(text: $cpf, placeholder: "000.000.000-00",
                           trailingIcon: "keyboard.badge.eye")
                .padding(.bottom, 16)
            
            FieldLabel("Senha")
            BrandSecureField(text: $password)
            
            HStack {
                Spacer()
                Button("Esqueci minha senha") { onForgotPassword() }
                    .font(brandTheme.font(size: 12, weight: .medium))
                    .foregroundColor(brandTheme.primary)
            }
            .padding(.bottom, 32)
            
            // Entrar
            Button(action: onLogin) {
                Text("Entrar")
                    .font(brandTheme.font(size: 15, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
            }
            .background(isFormValid ? brandTheme.primary : Color(hex: "6B7280").opacity(0.3))
            .cornerRadius(brandTheme.cornerRadius)
            .disabled(!isFormValid)
            .padding(.bottom, 12)
            
            // Biometria
            Button(action: onBiometric) {
                HStack(spacing: 8) {
                    Image(systemName: "touchid")
                        .frame(width: 18, height: 18)
                    Text("Entrar com biometria")
                        .font(brandTheme.font(size: 15, weight: .semiBold))
                }
                .foregroundColor(brandTheme.primary)
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .overlay(
                    RoundedRectangle(cornerRadius: brandTheme.cornerRadius)
                        .stroke(brandTheme.primary, lineWidth: 1)
                )
            }
            .padding(.bottom, 24)
            
            // Divisor e link de cadastro...
        }
        .padding(.horizontal, 24)
    }
    .background(brandTheme.background)
}
.navigationBarHidden(true)
```

---

## Checklist de Ajuste

- [ ] Header 64dp com `colorPrimary`, BrandLogo centralizado  
- [ ] `colorBackground` aplicado no corpo  
- [ ] Título 24sp bold com fonte da marca, letter-spacing -0.48  
- [ ] Subtítulo 13sp `colorOnSurface`, gap 24dp abaixo  
- [ ] Inputs com height 48dp, radius `borderRadiusDp`, border 1dp  
- [ ] Ícones de mask e eye 16×16dp, 15dp da borda direita  
- [ ] Labels de campo 12sp semibold `colorOnBackground`  
- [ ] Link "Esqueci" 12sp medium `colorPrimary` alinhado à direita  
- [ ] Gap de 32dp após campo senha antes do botão  
- [ ] Botão "Entrar" 52dp, radius, estado disabled (cinza opacity 30%)  
- [ ] Botão "Biometria" 52dp, border 1dp `colorPrimary`, ícone 18dp  
- [ ] Divisor "ou" com linhas `rgba(107,114,128,0.25)`  
- [ ] Link de cadastro centralizado, "Abra a sua grátis" em `colorPrimary`  
