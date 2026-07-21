# Evidências e Métricas

## Metodologia

As métricas foram coletadas por script local e pela suíte de testes do módulo compartilhado.

```bash
python3 scripts/measure_code_metrics.py
./gradlew :shared:allTests
```

A contagem considera arquivos de produção com extensões `.kt`, `.swift` e `.sq`. Testes em `commonTest` são contabilizados separadamente para não distorcer o denominador de código de produção.

## Resultado consolidado

| Indicador | Resultado |
|---|---:|
| Arquivos de produção analisados | 158 |
| LOC de produção | 10.765 |
| LOC no módulo `shared` | 2.220 |
| `shared` sobre o total | 20,6% |
| LOC em `shared/commonMain` | 1.514 |
| `commonMain` dentro de `shared` | 68,2% |
| Lógica de negócio em `commonMain` | 100% |
| Use cases e validadores compartilhados | 100% |
| Testes no módulo `shared` | 88 |
| Falhas nos testes | 0 |
| Plataformas executadas | Android JVM e iOS Simulator |

## Interpretação das métricas

O percentual total de código compartilhado não deve ser interpretado isoladamente. Como a UI foi implementada de forma nativa em Android e iOS, a maior parte das linhas do app fica naturalmente nas camadas visuais.

A métrica mais relevante para esta arquitetura é o compartilhamento das regras que não devem divergir:

| Camada | Estratégia |
|---|---|
| Domínio | Compartilhado em `commonMain` |
| Dados | Compartilhado em `commonMain` com drivers nativos quando necessário |
| Configuração | Compartilhada em `commonMain` |
| Segurança | Contrato compartilhado com implementação nativa |
| UI | Nativa por plataforma |

## Evidências visuais

As evidências abaixo estão versionadas em `docs/assets/screenshots/`:

| Tela | Evidência |
|---|---|
| Splash | `docs/assets/screenshots/splash-brands.png` |
| Login | `docs/assets/screenshots/login-brands.png` |
| Biometria | `docs/assets/screenshots/biometria-brands.png` |
| Home / Conta | `docs/assets/screenshots/home-conta-brands.png` |
| Home / Cartões | `docs/assets/screenshots/home-cartoes-brands.png` |
| Brand Switcher | `docs/assets/screenshots/brand-switcher-brands.png` |

## Cobertura funcional validada

| Área | Evidência técnica |
|---|---|
| Login | Use cases compartilhados e UI nativa nas duas plataformas |
| Biometria | Contrato comum com implementação nativa por plataforma |
| Conta | Estado compartilhado e formatação monetária comum |
| PIX | Validação de chaves, QR Code e comprovante no shared |
| White-label | Tokens e feature flags centralizados no shared |
| Testes | 88 testes executando em Android JVM e iOS Simulator |

## Lacunas técnicas identificadas

- Adicionar Kover para cobertura instrumentada de linhas no módulo `shared`.
- Ampliar testes dedicados para use cases ainda cobertos apenas indiretamente.
- Medir startup time, tamanho de APK/IPA e tempo de resposta de biometria em dispositivos físicos.
- Integrar as métricas em pipeline de CI corporativo.
- Atualizar referências do Figma para o arquivo oficial da empresa.

## Critérios sugeridos para continuidade

- Manter 100% das regras de domínio e validação em `commonMain`.
- Exigir testes em `commonTest` para novos use cases.
- Registrar métricas de build e testes a cada incremento relevante.
- Bloquear hardcode de tokens de marca nas UIs nativas.
- Validar Android e iOS no pipeline antes de promover builds internos.
