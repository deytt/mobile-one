# Referências de Design - mobile-one

Este documento centraliza as referências do Figma usadas pela POC. Ao migrar o projeto para o ambiente corporativo, preencher os campos abaixo com o arquivo oficial da empresa.

> Não registrar aqui links pessoais, arquivos temporários ou referências fora do domínio corporativo.

## Arquivo Figma

| Campo | Valor |
|---|---|
| Nome do arquivo | A definir no Figma corporativo |
| File key | A definir |
| URL base | A definir |
| Responsável | Design System / Mobile |
| Última atualização | A definir |

## Mapa de Telas

| Tela | Node ID corporativo | Spec vinculada | Observações |
|---|---|---|---|
| Splash | A definir | [SPEC-006](../specs/SPEC-006-splash-layout.md) | |
| Login | A definir | [SPEC-007](../specs/SPEC-007-login-layout.md) | |
| Biometria | A definir | [SPEC-008](../specs/SPEC-008-biometria-layout.md) | |
| Home / Conta | A definir | [SPEC-010](../specs/SPEC-010-home-conta-layout.md) | |
| Home / Cartões | A definir | [SPEC-009](../specs/SPEC-009-home-cartoes-layout.md) | |
| Brand Switcher | A definir | [SPEC-011](../specs/SPEC-011-brand-switcher-layout.md) | Tela interna para validação da configuração white-label |
| Fluxo PIX | A definir | [SPEC-003](../specs/SPEC-003-pix.md) | |

## Tokens de Marca

Os tokens abaixo são os valores atualmente implementados na POC e mapeados para `ThemeTokens` em `shared/commonMain`.

| Marca | Primary | Secondary | Background | Surface | On Primary | On Background | On Surface | Error | Fonte | Radius |
|---|---|---|---|---|---|---|---|---|---|---:|
| Banco Principal | `#003B6F` | `#F7941D` | `#F5F7FA` | `#FFFFFF` | `#FFFFFF` | `#1A1A2E` | `#6B7280` | `#DC2626` | Roboto | 12 |
| Fintech Verde | `#00A86B` | `#1A1A2E` | `#F0FAF5` | `#FFFFFF` | `#FFFFFF` | `#1A1A2E` | `#6B7280` | `#EF4444` | Inter | 16 |
| Banco Premium | `#782D00` | `#C9A84C` | `#FAFAF8` | `#FFFFFF` | `#FFFFFF` | `#1A1A1A` | `#6B6B6B` | `#B91C1C` | Georgia | 4 |

## Processo de Implementação

1. Atualizar este documento com o file key e os node IDs oficiais.
2. Revisar as specs de layout correspondentes.
3. Obter o contexto do Figma via MCP somente a partir do arquivo corporativo.
4. Mapear cores, tipografia, radius e espaçamentos para tokens do tema.
5. Evitar valores visuais hardcoded nas UIs; preferir `WhiteLabelConfig` e os temas nativos.
