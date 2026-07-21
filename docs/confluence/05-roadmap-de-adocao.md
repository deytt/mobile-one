# Roadmap de Adoção

## Objetivo

Este roadmap descreve uma estratégia incremental para evoluir a arquitetura KMP com UI nativa a partir da POC. As fases foram estruturadas para reduzir risco, preservar continuidade dos aplicativos e permitir avaliação por métricas ao final de cada etapa.

## Fase 0 - Fundação

**Objetivo:** consolidar infraestrutura KMP, build e testes compartilhados.

Entregas sugeridas:

- Configuração Gradle Multiplatform.
- Exportação do framework iOS.
- Integração com Android e iOS.
- Pipeline executando build e testes do `shared`.
- Primeiros contratos `expect/actual`.
- Documentação de setup e troubleshooting.

Critérios de saída:

- `./gradlew :shared:allTests` executando com sucesso.
- Android e iOS consumindo o framework compartilhado.
- CI validando pelo menos build Android e testes shared.

## Fase 1 - Domínio compartilhado

**Objetivo:** migrar validações e use cases sem alterar a UI existente.

Entregas sugeridas:

- Validadores de CPF, CNPJ, e-mail e chaves PIX.
- Use cases de autenticação e conta.
- Modelos de erro tipados.
- Testes em `commonTest` para cada regra.

Critérios de saída:

- Regras críticas com uma única implementação.
- Testes equivalentes executando em Android e iOS.
- Ausência de duplicidade funcional para regras migradas.

## Fase 2 - Dados e integração

**Objetivo:** centralizar repositórios, mappers, DTOs e cache.

Entregas sugeridas:

- Ktor para comunicação HTTP.
- SQLDelight para persistência local.
- Repositórios compartilhados.
- Estratégia offline-first.
- Mappers e contratos de erro padronizados.

Critérios de saída:

- Mesmas regras de cache, timeout e retry nas duas plataformas.
- Repositórios testáveis sem dispositivo físico.
- Observabilidade básica para falhas de integração.

## Fase 3 - Segurança e recursos nativos

**Objetivo:** padronizar contratos para biometria, armazenamento seguro e integridade.

Entregas sugeridas:

- `BiometricAuthenticator` com implementação Android e iOS.
- `SecureStorage` com Keystore e Keychain.
- `DeviceIntegrityChecker` por plataforma.
- Testes de contrato e validações manuais em dispositivos físicos.

Critérios de saída:

- Fluxos sensíveis usando APIs nativas.
- Contratos compartilhados estáveis para domínio.
- Documentação de limitações por plataforma.

## Fase 4 - UI nativa sobre shared

**Objetivo:** construir fluxos nativos consumindo contratos compartilhados.

Entregas sugeridas:

- Login e biometria.
- Home de conta e cartões.
- Fluxo PIX.
- Brand Switcher interno.
- Estados de loading, erro e vazio padronizados.

Critérios de saída:

- Paridade funcional entre Android e iOS.
- UI sem regras de negócio duplicadas.
- Tokens de marca aplicados via tema, sem hardcode.

## Fase 5 - White-label e operação

**Objetivo:** preparar a arquitetura para múltiplas marcas e evolução operacional.

Entregas sugeridas:

- Catálogo de marcas.
- Configuração bundled e/ou remota.
- Feature flags por marca.
- Pipeline parametrizado por flavor/marca.
- Validação visual por configuração.

Critérios de saída:

- Builds internos com múltiplas marcas.
- Processo claro para adicionar nova marca.
- Testes e screenshots por configuração relevante.

## Métricas de acompanhamento

| Métrica | Objetivo |
|---|---|
| Testes `commonTest` | Garantir consistência funcional entre plataformas |
| Cobertura de linhas no `shared` | Medir risco técnico de regras compartilhadas |
| Tempo de build | Avaliar impacto no ciclo de desenvolvimento |
| Startup time | Validar percepção de performance |
| Tamanho de binário | Monitorar impacto da arquitetura |
| Bugs por camada | Separar problemas de domínio, dados e UI |
| Esforço por feature | Medir produtividade após a fundação KMP |

## Próximos passos recomendados

1. Atualizar `docs/figma/design-system.md` com o Figma corporativo.
2. Adicionar Kover ao módulo `shared`.
3. Integrar testes compartilhados ao CI corporativo.
4. Medir performance em dispositivos físicos.
5. Definir uma feature piloto com critérios objetivos de aceite.
