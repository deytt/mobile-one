# Comparativo Técnico - KMP com UI Nativa vs React Native

## Objetivo

Este documento compara duas abordagens cross-platform para um aplicativo bancário: **React Native** e **Kotlin Multiplatform com UI nativa**. A análise considera critérios de arquitetura, segurança, experiência de usuário, testabilidade, adoção incremental e operação em ambiente regulado.

A intenção não é desqualificar uma tecnologia. React Native é uma alternativa madura e amplamente utilizada. A avaliação abaixo identifica em quais critérios a abordagem KMP com UI nativa se mostrou mais aderente ao contexto desta POC.

## Resumo comparativo

| Critério | React Native | KMP com UI nativa |
|---|---|---|
| Código compartilhado | Pode compartilhar UI e lógica em JavaScript/TypeScript | Compartilha domínio, dados, validações e configuração em Kotlin |
| UI | Implementada em camada cross-platform | Implementada com Compose e SwiftUI |
| Integração nativa | Exige módulos nativos para recursos específicos | Direta por plataforma, com contratos `expect/actual` quando necessário |
| Segurança | Requer atenção adicional em bridge, módulos nativos e exposição de lógica | Mantém fluxos sensíveis próximos das APIs nativas |
| Testabilidade de regras | Testes compartilhados dependem da arquitetura JS adotada | `commonTest` valida a mesma lógica para Android e iOS |
| Adoção incremental | Pode exigir maior reorganização quando a UI é compartilhada | Permite migrar domínio/dados por módulo mantendo UIs existentes |
| Experiência nativa | Boa, com cuidados e bibliotecas adequadas | Nativa por construção |
| Conhecimento de plataforma | Continua necessário para módulos, publicação e troubleshooting | Aproveita diretamente o conhecimento Android e iOS existente |

## Vantagens observadas em KMP com UI nativa

### 1. Compartilhamento da camada de maior risco funcional

Em aplicativos bancários, divergências em validação de CPF/CNPJ, chave PIX, limites, formatação monetária, estado de sessão e regras de autenticação podem gerar defeitos relevantes. Com KMP, essas regras ficam em `shared/commonMain`, com uma única implementação e uma suíte de testes comum.

A UI permanece por plataforma porque é a camada que mais depende de guidelines, acessibilidade, navegação, animações, permissões e comportamento de sistema operacional.

### 2. Integração direta com APIs sensíveis

Recursos como biometria, Keystore, Keychain, Secure Enclave, câmera e integridade do dispositivo são implementados com APIs nativas. O KMP permite declarar contratos comuns e implementar cada plataforma com `actual`, mantendo uma interface compartilhada para o domínio.

Esse modelo favorece auditoria, profiling e troubleshooting, pois os fluxos críticos permanecem próximos dos SDKs oficiais.

### 3. Experiência de usuário nativa

Jetpack Compose e SwiftUI permitem aderência direta aos padrões de cada plataforma. Isso é relevante para:

- acessibilidade;
- navegação;
- animações;
- teclado e máscaras de input;
- permissões;
- componentes de sistema;
- integração com biometria e câmera.

### 4. Evolução incremental

A abordagem permite iniciar pelo módulo compartilhado e migrar regras de negócio gradualmente. Android e iOS podem consumir o `shared` sem exigir reescrita simultânea de toda a UI.

Esse caminho reduz risco operacional e permite decisões baseadas em métricas coletadas ao longo da adoção.

### 5. White-label centralizado

A configuração white-label fica em `shared`, incluindo tokens, nomes, logos, feature flags e contatos de suporte. As UIs nativas apenas aplicam os tokens por meio dos temas de cada plataforma.

Esse modelo evita duplicidade de configuração e reduz risco de inconsistência visual ou funcional entre Android e iOS.

## Pontos de atenção da abordagem KMP

- O projeto mantém duas UIs, portanto ainda existe trabalho específico por plataforma na camada visual.
- APIs sem biblioteca multiplatform exigem contratos `expect/actual`.
- O build precisa validar Android e iOS no CI.
- Desenvolvedores iOS precisam compreender os contratos do módulo compartilhado.
- A adoção requer disciplina na separação entre domínio, dados e UI.

## Quando React Native pode ser mais adequado

React Native pode ser uma opção competitiva quando:

- a prioridade principal é compartilhar a maior parte da UI;
- o produto possui baixa dependência de APIs nativas sensíveis;
- o time já possui forte maturidade em TypeScript/React Native;
- a aplicação tem menor exigência de customização nativa por plataforma;
- o custo de manter duas UIs nativas supera os benefícios de experiência e integração.

## Conclusão técnica

Para esta POC, KMP com UI nativa apresentou maior aderência aos requisitos avaliados: consistência de regras de negócio, experiência nativa, integração segura com APIs de plataforma, white-label centralizado e adoção incremental.

A decisão recomendada é evoluir a avaliação com métricas adicionais de build, cobertura instrumentada, performance de inicialização, tamanho de binário e esforço de entrega por feature.
