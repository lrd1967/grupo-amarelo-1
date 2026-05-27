<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Relatório de Descoberta — Estágio 1: Arqueologia Digital

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **discovery-report**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Este documento consolida todas as descobertas do Estágio 1.
> Preencha cada seção com as conclusões do time. **Este é o input principal do Estágio 2** — sem ele, a especificação vira chute.

**Time**: Grupo Amarelo 1
**Data**: 27/05/2026
**Edição**: 1
**Participantes**: Time multidisciplinar dos 5 pares do workshop (PO/RE, EA/SA, TL/Dev, DBA/QA, DevOps/TW)

---

## 1. Sumário Executivo

> Em 3 a 5 frases, resuma o que o time descobriu sobre o SIFAP legado.
> O que é este sistema? Qual sua criticidade? Qual o estado do código?

O SIFAP é um sistema legado de missão crítica para cadastro, cálculo, validação, pagamento, conciliação e auditoria de benefícios sociais federais. A arqueologia confirmou forte centralidade do processamento batch e dos módulos de cálculo, com impacto direto em valor pago, conformidade e rastreabilidade. O código contém divergências relevantes entre comentário e implementação, além de bypasses e exceções históricas não documentadas formalmente. Foram catalogados 10 mistérios e localizados os 3 easter eggs esperados, incluindo política econômica antiga, validação especial de documentos e integração descontinuada. O estado geral do legado é funcional, porém com dívida técnica e risco elevado de regressão funcional se a modernização não preservar regras escondidas.

---

## 2. Visão Geral do Sistema

### 2.1 Propósito do SIFAP

O sistema gerencia o ciclo completo de benefícios sociais: cadastro de beneficiários e dependentes, parametrização de programas, validação documental e de elegibilidade, cálculo financeiro (benefício, correção e descontos), processamento mensal em lote, conciliação financeira e emissão de relatórios operacionais e de auditoria.

### 2.2 Arquitetura Legada

A arquitetura legada combina Natural 6.3 com Adabas 7.4 em mainframe, com 15 programas Natural e 4 DDMs centrais (BENEFICIARIO, PROGRAMA-SOCIAL, PAGAMENTO, AUDITORIA). O fluxo principal é: cadastro/validação online -> processamento batch mensal (BATCHPGT) -> conciliação (BATCHCON) -> relatórios gerenciais e de auditoria (BATCHREL, RELPGT, RELAUDIT). Os módulos de cálculo (CALCBENF, CALCCORR, CALCDSCT) e validação (VALBENEF, VALDOCS, VALELEG) concentram regras de negócio críticas e apresentam acoplamento alto com risco de efeito cascata.

### 2.3 Usuários e Perfis

Os principais usuários são operadores de processamento da CGPB, analistas de fiscalização da DEFIS, áreas gestoras de programas (MDAS/SENARC) e perfis técnicos de suporte/infraestrutura. Em termos funcionais, os perfis observados no legado se agrupam em: cadastro e manutenção, validação/elegibilidade, operação batch, conciliação financeira e auditoria/relatórios.

---

## 3. Principais Descobertas

### 3.1 Regras de Negócio Críticas

> Liste as 5 regras de negócio mais importantes encontradas.

1. BR-001: O processamento mensal deve aplicar regras de cálculo e desconto consistentes entre canais, evitando lógica simplificada divergente no batch (derivado de MYS-001).
2. BR-002: O cálculo do 13o benefício deve respeitar proporcionalidade temporal definida pela regra de negócio, não substituída por fator inadequado (derivado de MYS-002).
3. BR-003: A correção retroativa deve acumular índices ao longo de todo o período solicitado, com rastreabilidade da composição (derivado de MYS-003).
4. BR-004: A trilha de auditoria deve incluir eventos de exclusão para preservar integridade de investigação e compliance (derivado de MYS-004).
5. BR-005: A proteção de CPF deve mascarar dados de forma uniforme, sem caminhos que exponham parcialmente informação sensível (derivado de MYS-005).

### 3.2 Dependências Complexas

> Quais programas estão mais acoplados? Onde há risco de efeito cascata?

O programa mais acoplado no fluxo de negócio é BATCHPGT, por orquestrar cálculo, validação e atualização de pagamentos em volume elevado. Há dependência funcional forte entre BATCHPGT e CALCBENF/CALCCORR/CALCDSCT, com risco de regressão financeira se qualquer regra for migrada parcialmente. No domínio de qualidade e segurança, VALDOCS e VALELEG concentram exceções que podem afetar elegibilidade e antifraude em cascata. Em relatórios, RELAUDIT e BATCHREL influenciam controle e governança; alterações sem equivalência funcional podem comprometer trilha de auditoria e visão gerencial.

### 3.3 Dívida Técnica Identificada

> Que problemas no código legado vão complicar a migração?

- [x] Divergência entre comentário e implementação em rotinas críticas de cálculo e processamento.
- [x] Regras hardcoded e exceções históricas (prefixos especiais, região especial, blocos comentados legados).
- [x] Presença de código morto/descontinuado e ausência de segregação clara entre regra de domínio e detalhe técnico.

### 3.4 Gaps de Documentação

> O que a documentação existente NÃO cobre?

A documentação histórica não cobre adequadamente os módulos de cálculo e batch mais críticos, nem explica exceções operacionais ainda ativas no código. Também há lacunas sobre justificativa de regras especiais (ex.: bypass documental e elegibilidade por região especial), além de divergência entre cabeçalhos/comentários e o comportamento efetivo executado. Não há documentação consolidada e atual das regras escondidas para suporte ao desenho de EARS sem arqueologia prévia.

---

## 4. Mistérios e Riscos

### 4.1 Mistérios Não Resolvidos

> Resuma os mistérios do arquivo `mysteries-found.md` que permanecem sem explicação.

| ID  | Descrição | Risco para Migração |
| --- | --------- | ------------------- |
| MYS-001 | Batch com cálculo simplificado local em desacordo com objetivo declarado de reutilizar rotinas oficiais. | Quebra de equivalência entre canais e inconsistência contábil. |
| MYS-002 | Fórmula de 13o no comentário difere da fórmula executada. | Erro financeiro recorrente e passivo regulatório. |
| MYS-003 | Correção retroativa aparentemente não acumula todo período esperado. | Sub/supercorreção de retroativos e disputa de valores. |
| MYS-004 | Relatório de auditoria filtra exclusões (acao EX). | Perda de rastreabilidade e fragilidade de compliance. |
| MYS-005 | Máscara de CPF expõe primeiros dígitos em caminho específico. | Exposição de dado sensível e risco de LGPD. |
| MYS-006 | Consulta de histórico limita em 12 sem garantir recência. | Decisão operacional com histórico potencialmente defasado. |
| MYS-007 | Prefixos especiais em documentos forçam validação positiva e zeram erros. | Bypass antifraude e aprovação indevida. |
| MYS-008 | Região 99 concede elegibilidade e encerra rotina antecipadamente. | Concessão indevida sem validações completas. |
| MYS-009 | Possível off-by-one no limite de dependentes. | Inconsistência de cadastro e cálculos derivados. |
| MYS-010 | Sumarização prometida por programa não aparece na saída batch. | Visão gerencial incompleta para gestão de políticas públicas. |

### 4.2 Riscos para o Estágio 2

> O que o time de especificação precisa saber antes de começar?

1. Risco de especificar comportamento com base em comentários legados em vez do comportamento real executado.
2. Risco de perder exceções de negócio ocultas no código (inclusive indevidas) e gerar regressões funcionais/sanitárias na migração.
3. Risco de subestimar impacto de acoplamento entre batch, cálculo e validações, causando efeito cascata entre pagamento, auditoria e conciliação.

---

## 5. Recomendações

### 5.1 O que migrar primeiro

> Com base na priorização do Par 1 (Product Owner), quais funcionalidades devem ser migradas primeiro?

| Prioridade | Funcionalidade | Justificativa |
| ---------- | -------------- | ------------- |
| 1          | Processamento mensal de pagamentos (BATCHPGT + CALCBENF/CALCCORR/CALCDSCT) | Núcleo financeiro do sistema; maior risco de impacto ao cidadão e ao orçamento. |
| 2          | Validação de elegibilidade e documentos (VALELEG + VALDOCS + VALBENEF) | Controles de entrada e antifraude precisam ser preservados com saneamento de exceções inseguras. |
| 3          | Trilha de auditoria e conciliação (RELAUDIT + BATCHCON) | Sustenta compliance, investigação e fechamento financeiro entre sistemas. |

### 5.2 O que descartar

> Funcionalidades que provavelmente não precisam ser migradas:

- Integração legada descontinuada com Banco Real (bloco comentado em BATCHCON): não migrar, manter apenas registro histórico em documentação técnica.
- Dependência implícita de ordenação física para exibição de histórico: descartar como comportamento intencional e substituir por ordenação explícita por recência.

### 5.3 O que evoluir

> Funcionalidades que devem ser migradas E melhoradas:

- Máscara de CPF em consultas: padronizar anonimização sem exceções, com testes de segurança e conformidade LGPD.
- Relatório de auditoria: incluir exclusões (acao EX) e trilha completa com filtros configuráveis por perfil.
- Validação documental especial: substituir bypass por feature flag auditável e política formal de exceções com expiração.
- Elegibilidade por região especial: manter regra apenas se formalizada em política vigente e com justificativa rastreável.
- Sumarização gerencial batch: incluir corte por programa social com reconciliação dos totais por região/status.

---

## 6. Métricas do Estágio

| Métrica                       | Valor        |
| ----------------------------- | ------------ |
| Programas analisados          | 15 / 15      |
| DDMs mapeados                 | 4 / 4        |
| Regras de negócio encontradas | 10           |
| Regras escondidas encontradas | 10 / 10      |
| Easter eggs encontrados       | 3 / 3        |
| Termos no glossário           | 0            |
| Mistérios catalogados         | 10           |
| Tempo total gasto             | 3 horas      |

---

## 7. Notas para o Próximo Estágio

> Deixe aqui mensagens para o time no Estágio 2 (Especificação Moderna):

Antes de escrever EARS, tratar cada mistério como hipótese obrigatória de validação funcional com source_legacy explícito por faixa de linhas. Priorizar equivalência comportamental no núcleo financeiro e de elegibilidade; melhorias devem vir somente após baseline de paridade aprovado em testes. Registrar no Estágio 2 quais exceções serão preservadas, saneadas ou removidas por decisão de negócio (com ADR quando necessário). Para qualquer regra ambígua entre comentário e execução, considerar o comportamento efetivo do código como fonte primária e abrir item de decisão para Product Owner/Requirements Engineer.

---

## Definição de Pronto deste relatório

- [x] Todas as seções acima preenchidas (sem placeholders).
- [x] Pelo menos 5 regras críticas listadas em §3.1, cada uma referenciando uma `BR-XXX` do catálogo.
- [x] Decisões de migrar/descartar/evoluir em §5 cobrem as 8+ funcionalidades principais.
- [ ] Métricas de §6 conferem com os outros artefatos (glossary.md, business-rules-catalog.md, mysteries-found.md).

— Paula


---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-found.md"><strong>mysteries-found.md</strong></a><br/>
<sub>Lista de mistérios.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="../02-spec-moderna/GUIDE.md"><strong>Estágio 2 — Spec</strong></a><br/>
<sub>Próximo estágio: spec moderna.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>

