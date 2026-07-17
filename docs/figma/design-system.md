# Figma Design System — mobile-one

**Arquivo:** Mobile-One  
**File Key:** `i0v5vLAdG0PMWZ6bYwUb0h`  
**Versão:** v2.0 — 7 telas, 3 marcas  
**URL base:** `https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One`

---

## Mapa de Nós por Tela

| Tela | node-id | Spec vinculada | URL dev mode |
|---|---|---|---|
| Splash | `28:19512` | — | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=28-19512&m=dev) |
| Login | `29:20015` | [SPEC-001](../specs/SPEC-001-login-biometrico.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-20015&m=dev) |
| Biometria | `29:20689` | [SPEC-001](../specs/SPEC-001-login-biometrico.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-20689&m=dev) |
| Home / Conta | `29:21243` | [SPEC-002](../specs/SPEC-002-saldo-extrato.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-21243&m=dev) |
| Cartões | `29:22301` | [SPEC-002](../specs/SPEC-002-saldo-extrato.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-22301&m=dev) |
| Brand Switcher | `29:23293` | [SPEC-004](../specs/SPEC-004-white-label-config.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=29-23293&m=dev) |
| Fluxo PIX | `15:9848` | [SPEC-003](../specs/SPEC-003-pix.md) | [dev mode](https://www.figma.com/design/i0v5vLAdG0PMWZ6bYwUb0h/Mobile-One?node-id=15-9848&m=dev) |

---

## Tokens de Cor por Marca

Os tokens abaixo mapeiam diretamente para `ThemeTokens` em `shared/commonMain/config/WhiteLabelConfig.kt`.

### Banco Principal (`brandId = "banco_principal"`)

| Token Figma | Campo Kotlin | Valor |
|---|---|---|
| `color/primary` | `colorPrimary` | `#003B6F` |
| `color/secondary` | `colorSecondary` | `#F7941D` |
| `color/background` | `colorBackground` | `#F5F7FA` |
| `color/surface` | `colorSurface` | `#FFFFFF` |
| `color/on-primary` | `colorOnPrimary` | `#FFFFFF` |
| `color/on-bg` | `colorOnBackground` | `#1A1A2E` |
| `color/on-surface` | `colorOnSurface` | `#6B7280` |
| `color/error` | `colorError` | `#DC2626` |
| Tipografia | `fontFamily` | `Roboto` |
| Border radius | `borderRadiusDp` | `12` |

### Fintech Verde (`brandId = "fintech_verde"`)

| Token Figma | Campo Kotlin | Valor |
|---|---|---|
| `color/primary` | `colorPrimary` | `#00A86B` |
| `color/secondary` | `colorSecondary` | `#1A1A2E` |
| `color/background` | `colorBackground` | `#F0FAF5` |
| `color/surface` | `colorSurface` | `#FFFFFF` |
| `color/on-primary` | `colorOnPrimary` | `#FFFFFF` |
| `color/on-bg` | `colorOnBackground` | `#1A1A2E` |
| `color/on-surface` | `colorOnSurface` | `#6B7280` |
| `color/error` | `colorError` | `#EF4444` |
| Tipografia | `fontFamily` | `Inter` |
| Border radius | `borderRadiusDp` | `16` |

### Banco Premium (`brandId = "banco_premium"`)

| Token Figma | Campo Kotlin | Valor |
|---|---|---|
| `color/primary` | `colorPrimary` | `#782D00` |
| `color/secondary` | `colorSecondary` | `#C9A84C` |
| `color/background` | `colorBackground` | `#FAFAF8` |
| `color/surface` | `colorSurface` | `#FFFFFF` |
| `color/on-primary` | `colorOnPrimary` | `#FFFFFF` |
| `color/on-bg` | `colorOnBackground` | `#1A1A1A` |
| `color/on-surface` | `colorOnSurface` | `#6B6B6B` |
| `color/error` | `colorError` | `#B91C1C` |
| Tipografia | `fontFamily` | `Georgia` |
| Border radius | `borderRadiusDp` | `4` |

---

## Descrição Visual por Tela

### Splash
- Fundo: gradiente sobre `color/primary` (mais claro no centro, mais escuro nas bordas)
- Centro: logo da marca (shape + iniciais em branco) + nome da marca em `color/on-primary`
- Rodapé: "Seguro e regulado pelo Banco Central do Brasil" em `color/on-primary` com 50% opacidade
- Sem status bar visível, sem bottom navigation

### Login
- Header: `color/primary` com logo e nome da marca centralizados
- Corpo em `color/background`:
  - Título "Bem-vindo de volta" em `color/on-bg`, bold
  - Subtítulo "Entre com seu CPF e senha" em `color/on-surface`
  - Campo CPF com placeholder `000.000.000-00` e ícone de teclado
  - Campo Senha com placeholder bullets e ícone de olho
  - Link "Esqueci minha senha" à direita em `color/primary`
  - Botão "Entrar" — **desabilitado** (cinza) enquanto campos inválidos; habilitado com `color/primary`
  - Botão outlined "Entrar com biometria" com ícone de fingerprint e borda `color/primary`
  - Divider "ou" em `color/on-surface` 40% opacidade
  - Link "Abra a sua grátis" em `color/primary`

### Biometria
- Header: igual ao Login
- Corpo em `color/background`:
  - Avatar circular "HB" com fundo `color/primary`, tamanho grande (~72dp)
  - "Olá, Heitor!" em `color/on-bg`, bold, centralizado
  - Subtítulo em `color/on-surface`, centralizado
  - Ícone de fingerprint grande com círculo de fundo `color/primary` 10% opacidade
  - Label "Toque para usar biometria" em `color/primary`
  - Link "Usar CPF e senha" em `color/on-surface` no rodapé

### Home / Conta
- Header: `color/primary` com avatar circular (iniciais), nome "Olá, Heitor" e ícone de sino
- Saldo: "Saldo disponível" + valor grande + ícone olho, tudo sobre `color/primary`
- 3 botões de ação rápida (Pagar, Extrato, PIX): ícones sobre fundo circular semi-transparente branco
- Seções PIX e Open Finance: cards em `color/surface` com ícone, título, descrição e seta `>`
- "Transações recentes" com link "Ver todas" em `color/secondary`
- Lista de transações: ícone de play/categoria, nome, data, valor
- Bottom navigation: Cartões | **Conta** (ativa, pill `color/primary`) | ícone grid

### Cartões
- Header: `color/primary` com avatar, nome e sino
- Card "Fatura aberta": fundo `color/primary` levemente transparente, valor da fatura, datas
- Botões "Pagar fatura" e "Meus cartões": outlined com `color/primary`, fundo `color/surface`
- Seção "Meu limite": label, valores utilizado/disponível, barra de progresso `color/primary`
- "Minhas compras" com "Ver todas" em `color/secondary`
- Lista de compras: ícone categoria, nome, data/tipo, valor
- Bottom navigation: **Cartões** (ativa) | Conta | ícone grid

### Brand Switcher
- Fundo escuro fixo `#1A1A2E` (não usa tokens da marca — é tela de dev)
- Header: "Brand Switcher" + badge "DEV MODE" laranja + subtítulo "Modo de demonstração"
- Seção "SELECIONAR MARCA": 3 itens com radio button, chip de cor primary, nome da marca, tipografia + radius, chips primary e secondary com hex
- Botão "Aplicar marca": fundo `#003B6F` fixo, texto branco
- Rodapé: "As mudanças são aplicadas instantaneamente em todo o app"

---

## Navegação do App (fluxo de telas)

```
[Abertura]
    │
    └──→ [Splash] (~2s)
              │
              ├──[sem sessão]──→ [Login]
              │                      │
              │                      ├──[credenciais OK]──→ [Home/Conta]
              │                      └──[tap biometria]──→ [Biometria]──→ [Home/Conta]
              │
              └──[sessão salva + biometria]──→ [Biometria]
                                                    │
                                                    ├──[OK]──→ [Home/Conta]
                                                    └──[Usar CPF e senha]──→ [Login]

[Home/Conta]
    │
    ├──[aba Cartões]──→ [Cartões]
    ├──[botão PIX]──→ [Fluxo PIX] (7 telas — ver node 15:9848)
    └──[gesto secreto: 5 toques no logo]──→ [Brand Switcher]

[Brand Switcher]
    └──[Aplicar marca]──→ reinicia fluxo com nova brand ──→ [Splash]
```

---

## Como usar este documento para implementação

Antes de implementar qualquer tela:
1. Localizar o `node-id` da tela neste documento
2. Chamar `get_design_context` no MCP do Figma com o `fileKey` e `nodeId`
3. Adaptar o output React/Tailwind para Compose (Android) ou SwiftUI (iOS)
4. Mapear cada cor hardcoded do output para o token correspondente via `WhiteLabelConfig`
5. Nunca usar hex diretamente no código de UI — sempre via tema
