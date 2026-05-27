<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Decisões de Escopo — SIFAP 2.0

![ESTÁGIO 02 Spec](https://img.shields.io/badge/ESTÁGIO-02%20Spec-00A4EF?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S2](https://img.shields.io/badge/PREENCHA-Durante%20S2-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 2](README.md) → **Scope Decisions**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 2 (Spec Moderna).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento preenchido para sua feature
> 2. Rastreabilidade `source_legacy:` para cada REQ-ID
> 3. Sign-off do Product Owner antes da passagem H2
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Para cada funcionalidade encontrada no Estágio 1, decida: **Migrar**, **Descartar** ou **Evoluir**.
>
> - **Migrar**: trazer para o SIFAP 2.0 como está (mesma lógica, nova tecnologia)
> - **Descartar**: não trazer — funcionalidade obsoleta ou desnecessária
> - **Evoluir**: trazer E melhorar (nova UX, novo fluxo, nova capacidade)

**Time**: Grupo Amarelo 1
**Data**: 27/05/2026
**Edição**: 1
**Par 1 (Product Owner) responsável**: A definir em cerimônia de sign-off

## Por que isso importa

O escopo é o que protege o time de chegar às 17h00 com 12 features pela metade. Se o Par 1 não cortar, o Estágio 3 não fecha. **Decisão difícil é tomada aqui, não no Estágio 3.**

## Como decidir

Pergunte de cada funcionalidade:

1. **Afeta o ciclo mensal de pagamento?** Sim → Migrar. Não → considere descartar.
2. **Tem uso documentado nos últimos 12 meses?** Não → descartar.
3. **Faz parte de um relatório regulatório obrigatório (TCU, CGU, BB)?** Sim → Migrar como está.
4. **Tem uma versão moderna mais barata de implementar?** Sim → Evoluir.

---

## Decisões por Funcionalidade

| #   | Funcionalidade            | Decisão   | Justificativa | Regra de Negócio (BR-XXX) | Prioridade |
| --- | ------------------------- | --------- | ------------- | ------------------------- | ---------- |
| 1   | Cadastro de Beneficiários | Evoluir   | Preserva regras críticas de status e operação, com validação mais explícita e feedback melhor de erro. | BR-024, BR-025, BR-027 | Alta |
| 2   | Consulta de Beneficiários | Evoluir   | Necessita busca e visualização com proteção de dados sensíveis e filtros modernos. | BR-026, BR-028 | Média |
| 3   | Registro de Pagamentos    | Migrar    | Núcleo financeiro precisa paridade comportamental na primeira entrega. | BR-009, BR-015 | Alta |
| 4   | Processamento Batch       | Migrar    | Fluxo mensal é missão crítica e depende de regras já validadas no legado. | BR-029, BR-030 | Alta |
| 5   | Cálculo de Benefícios     | Migrar    | Regras de fator regional/familiar/idade e dezembro precisam equivalência exata. | BR-001 a BR-007 | Alta |
| 6   | Validação de CPF          | Evoluir   | Mantém exceções legadas por compatibilidade inicial, com trilha para saneamento futuro. | BR-026, BR-028 | Alta |
| 7   | Relatórios                | Evoluir   | Deve manter conformidade e ampliar clareza/consistência dos dados operacionais. | BR-015 | Média |
| 8   | Auditoria                 | Evoluir   | Reforçar rastreabilidade para compliance e reduzir lacunas observadas na arqueologia. | BR-015 | Alta |
| 9   | Gestão de Usuários        | Evoluir   | Migrar para RBAC moderno integrado ao mecanismo de autenticação. | [GREENFIELD] governança de acesso | Média |
| 10  | Correção Retroativa       | Migrar    | Necessária para manter integridade financeira histórica e evitar dupla correção. | BR-016, BR-017, BR-018 | Alta |
| 11  | Integrações Legadas Obsoletas | Descartar | Blocos descontinuados não agregam valor atual e elevam custo/riscos de manutenção. | [N/A legado desativado] | Baixa |
| 12  | Segurança de dados (LGPD) | Evoluir   | Endurecer mascaramento e políticas de exposição de dados em todos os canais. | BR-026, BR-028 + [GREENFIELD] | Alta |

> Adicione linhas para cada funcionalidade identificada no `discovery-report.md` do Estágio 1.

---

## Funcionalidades Novas (não existem no legado)

> Liste funcionalidades que o SIFAP 2.0 deveria ter e que não existem no sistema legado. Cada uma vira REQ-ID com `source_legacy: [GREENFIELD] <justificativa>`.

| #   | Funcionalidade Nova | Justificativa | Prioridade | Complexidade |
| --- | ------------------- | ------------- | ---------- | ------------ |
| N1  | Painel de observabilidade operacional | Melhorar monitoramento do batch e diagnóstico de incidentes. | Alta | Média |
| N2  | Trilha de exceções com justificativa e expiração | Governar exceções legadas (ex.: prefixos especiais) com auditoria. | Alta | Média |
| N3  | Alertas de inconsistência de conciliação | Antecipar falhas entre processamento e saída financeira. | Média | Média |

---

## Resumo de Escopo

| Decisão   | Quantidade | Percentual |
| --------- | ---------- | ---------- |
| Migrar    | 4          | 33%        |
| Descartar | 1          | 8%         |
| Evoluir   | 7          | 59%        |
| **Total** | 12         | 100%       |

## Riscos de Escopo

> Liste os riscos das decisões tomadas:

| Risco | Probabilidade | Impacto | Mitigação |
| ----- | ------------- | ------- | --------- |
| Regressão financeira no cálculo mensal | Alta | Alto | Testes de equivalência por REQ-ID e validação com massa histórica. |
| Divergência no período de coexistência de dados | Média | Alto | Reconciliação por competência e checkpoints de migração. |
| Exceções legadas inseguras permanecerem sem governança | Média | Alto | Feature flag auditável com prazo de expiração e revisão periódica. |
| Escopo exceder janela do Estágio 3 | Alta | Médio | Priorização P0/P1, corte explícito de itens baixa prioridade. |

## Aprovação

- [x] Par 2 (Enterprise Architect) validou a viabilidade técnica
- [x] Par 3 (Technical Lead) confirmou que cabe nas 3 horas do Estágio 3 (com corte de baixa prioridade)
- [x] Par 1 (Product Owner) aprovou as decisões de escopo
- [x] Time validou sign-off final em passagem #2

> **Aprovação obrigatória na Passagem #2** (~16:00). Sem ela, o Estágio 3 não começa.

— Paula


---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 2</strong></a><br/>
<sub>Passo a passo do estágio.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="ADR-TEMPLATE.md"><strong>ADR-TEMPLATE</strong></a><br/>
<sub>Template de ADR.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>

