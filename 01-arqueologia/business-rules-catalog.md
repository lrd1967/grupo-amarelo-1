<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Catálogo de Regras de Negócio — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **business-rules-catalog**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui todas as regras de negócio extraídas do código Natural/Adabas.
> Cada regra precisa ter rastreabilidade até o código-fonte.
>
> **REGRA DURA:** linhas com `Programa Fonte` vazio são **inválidas** e não contam para o gate do Estágio 2. Use o formato `01-arqueologia/legado-sifap/natural-programs/ARQUIVO.NSN#L<inicio>-L<fim>` sempre que possível. Mínimo aceito: nome do arquivo .NSN.

## Como pensar em "regra de negócio"

O que conta:

- Um `IF` que decide algo no domínio (ex.: _"se a UF é do Nordeste e o programa é Seca, valor base × 1.2"_)
- Uma constante numérica sem explicação (ex.: `0.075` num cálculo de imposto)
- Uma transição de status com regra (ex.: _"só de A para S, nunca de I para A"_)
- Um tratamento especial para um caso (ex.: _"se o CPF começa com 999, é teste"_)

O que NÃO conta: paginação de relatório, formatação de saída, manipulação de cursor Adabas, abertura de arquivo. Ignore esses detalhes de implementação.

## Níveis de Risco

| Nível       | Descrição                                                     |
| ----------- | ------------------------------------------------------------- |
| **CRÍTICO** | Regra financeira ou de segurança - erro causa prejuízo direto |
| **ALTO**    | Regra de negócio central — afeta fluxo principal              |
| **MÉDIO**   | Regra de validação ou formatação — afeta qualidade dos dados  |
| **BAIXO**   | Regra de apresentação ou conveniência — impacto limitado      |

## Inventário por Programa

| Programa (Autor, Última modificação) | Inputs (DDMs lidas) | Outputs (DDMs escritas) | Cadeia CALLNAT |
| --- | --- | --- | --- |
| BATCHCON (Marcos Antonio Ribeiro, 2014) | PAGAMENTO, AUDITORIA | PAGAMENTO (UPDATE), AUDITORIA (STORE) | Nenhuma ocorrência de CALLNAT |
| BATCHPGT (Carlos Roberto da Silva, 2015) | BENEFICIARIO, PAGAMENTO, PROGRAMA-SOCIAL | PAGAMENTO (STORE) | Nenhuma ocorrência de CALLNAT |
| BATCHREL (Patricia Gomes de Souza, 2013) | PAGAMENTO, BENEFICIARIO | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| CADBENEF (Carlos Roberto da Silva, 2011) | BENEFICIARIO | BENEFICIARIO (STORE/UPDATE) | Nenhuma ocorrência de CALLNAT |
| CADDEPEND (Ana Lucia Pereira, 2008) | BENEFICIARIO | BENEFICIARIO (UPDATE) | Nenhuma ocorrência de CALLNAT |
| CADPROG (Marcos Antonio Ribeiro, 2012) | PROGRAMA-SOCIAL | PROGRAMA-SOCIAL (STORE) | Nenhuma ocorrência de CALLNAT |
| CALCBENF (Carlos Roberto da Silva, 2013) | BENEFICIARIO, PAGAMENTO, PROGRAMA-SOCIAL | PAGAMENTO (STORE) | Nenhuma ocorrência de CALLNAT |
| CALCCORR (Patricia Gomes de Souza, 2014) | PAGAMENTO | PAGAMENTO (UPDATE) | Nenhuma ocorrência de CALLNAT |
| CALCDSCT (Roberto Mendes Junior, 2015) | PAGAMENTO, BENEFICIARIO | PAGAMENTO (UPDATE) | Nenhuma ocorrência de CALLNAT |
| CONSBENF (Marcia Helena Oliveira, 2012) | BENEFICIARIO, PAGAMENTO | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| RELAUDIT (Roberto Mendes Junior, 2014) | AUDITORIA | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| RELPGT (Ana Lucia Pereira, 2010) | PAGAMENTO, BENEFICIARIO | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| VALBENEF (Marcia Helena Oliveira, 2010) | BENEFICIARIO (VIEW declarada) | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| VALDOCS (Ana Lucia Pereira, 2011) | BENEFICIARIO (VIEW declarada) | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |
| VALELEG (Jose Ferreira dos Santos, 2013) | BENEFICIARIO, PROGRAMA-SOCIAL | Nenhuma escrita em DDM | Nenhuma ocorrência de CALLNAT |

## Regras Encontradas

| ID     | Regra de Negócio | Programa Fonte | Campos DDM | Nível de Risco | Notas |
| ------ | ---------------- | -------------- | ---------- | -------------- | ----- |
| BR-001 | Conciliacao aplica status de pagamento conforme codigo de retorno bancario (00 pago, 01 devolvido, 02 estornado). | [BATCHCON.NSN L171-L194](legado-sifap/natural-programs/BATCHCON.NSN#L171-L194) | PAGAMENTO.STATUS-PGTO, PAGAMENTO.COD-RETORNO | ALTO | Regra central de baixa/retorno CNAB. |
| BR-002 | Apenas beneficiarios ativos e programas ativos entram no lote de geracao. | [BATCHPGT.NSN L195-L198](legado-sifap/natural-programs/BATCHPGT.NSN#L195-L198), [BATCHPGT.NSN L227-L230](legado-sifap/natural-programs/BATCHPGT.NSN#L227-L230) | BENEFICIARIO.STATUS, PROGRAMA-SOCIAL.STATUS-PROG | ALTO | Filtra elegibilidade operacional do batch mensal. |
| BR-003 | Faixas de COD-REGIAO determinam macro-regiao para sumarizacao do relatorio. | [BATCHREL.NSN L117-L132](legado-sifap/natural-programs/BATCHREL.NSN#L117-L132) | BENEFICIARIO.COD-REGIAO | MÉDIO | Regras: 1-5, 6-10, 11-15, 16-20, demais. |
| BR-004 | Beneficiario com idade acima de 75 recebe status suspenso no cadastro. | [CADBENEF.NSN L166-L168](legado-sifap/natural-programs/CADBENEF.NSN#L166-L168) | BENEFICIARIO.DT-NASCIMENTO, BENEFICIARIO.STATUS | ALTO | Ajuste explicitado no historico de alteracoes. |
| BR-005 | Inclusao de dependentes bloqueia quando atinge limite maximo permitido. | [CADDEPEND.NSN L63-L65](legado-sifap/natural-programs/CADDEPEND.NSN#L63-L65) | BENEFICIARIO.NUM-DEPENDENTES, BENEFICIARIO.DEPENDENTES | ALTO | Evita extrapolar capacidade do grupo PE. |
| BR-006 | Operacao de cadastro de programa aceita apenas I (inclusao) ou C (consulta). | [CADPROG.NSN L50-L53](legado-sifap/natural-programs/CADPROG.NSN#L50-L53) | PROGRAMA-SOCIAL.COD-PROGRAMA | MÉDIO | Entrada fora do dominio encerra a rotina. |
| BR-007 | Calculo de beneficio bloqueia beneficiario nao ativo antes de gerar pagamento. | [CALCBENF.NSN L160-L161](legado-sifap/natural-programs/CALCBENF.NSN#L160-L161) | BENEFICIARIO.STATUS, PAGAMENTO.STATUS-PGTO | ALTO | Nao gera parcela para status diferente de A. |
| BR-008 | Correcao monetaria multiplica indice acumulado com a serie IPCA mensal. | [CALCCORR.NSN L54-L68](legado-sifap/natural-programs/CALCCORR.NSN#L54-L68), [CALCCORR.NSN L185-L185](legado-sifap/natural-programs/CALCCORR.NSN#L185) | PAGAMENTO.VLR-BRUTO, PAGAMENTO.VLR-LIQUIDO | CRÍTICO | Regra financeira com impacto direto em valor corrigido. |
| BR-009 | Total de descontos e limitado a 30% do bruto, exceto tipo judicial. | [CALCDSCT.NSN L101-L105](legado-sifap/natural-programs/CALCDSCT.NSN#L101-L105), [CALCDSCT.NSN L164-L167](legado-sifap/natural-programs/CALCDSCT.NSN#L164-L167) | PAGAMENTO.VLR-BRUTO, PAGAMENTO.VLR-DESCONTO, BENEFICIARIO.TIPO-DSCT | CRÍTICO | Teto financeiro explicito na rotina. |
| BR-010 | Consulta assume busca por CPF quando tipo de busca vem em branco. | [CONSBENF.NSN L80-L81](legado-sifap/natural-programs/CONSBENF.NSN#L80-L81) | BENEFICIARIO.CPF, BENEFICIARIO.NIS | MÉDIO | Default funcional da tela de consulta. |
| BR-011 | Relatorio de auditoria aplica filtros opcionais de acao, usuario e tabela. | [RELAUDIT.NSN L111-L128](legado-sifap/natural-programs/RELAUDIT.NSN#L111-L128) | AUDITORIA.ACAO, AUDITORIA.USUARIO, AUDITORIA.TABELA-REF | MÉDIO | Regra de selecao de eventos para exibicao. |
| BR-012 | Relatorio de pagamentos filtra programa quando COD-PROG-FILTRO for diferente de zero. | [RELPGT.NSN L87-L88](legado-sifap/natural-programs/RELPGT.NSN#L87-L88) | PAGAMENTO.COD-PROGRAMA | MÉDIO | Valor 0 significa todos os programas. |
| BR-013 | Validacao cadastral aceita apenas status A, S, C, I ou D. | [VALBENEF.NSN L164-L167](legado-sifap/natural-programs/VALBENEF.NSN#L164-L167) | BENEFICIARIO.STATUS | ALTO | Fora do dominio gera erro de validacao. |
| BR-014 | Validacao documental incrementa contador de erros quando CPF e invalido. | [VALDOCS.NSN L68-L71](legado-sifap/natural-programs/VALDOCS.NSN#L68-L71) | BENEFICIARIO.CPF | MÉDIO | Compõe resultado final de consistencia documental. |
| BR-015 | Regiao especial 99 marca beneficiario como elegivel imediatamente. | [VALELEG.NSN L105-L109](legado-sifap/natural-programs/VALELEG.NSN#L105-L109) | BENEFICIARIO.COD-REGIAO, BENEFICIARIO.STATUS | ALTO | Excecao explicita para internacional/diplomatico. |

> Adicione mais linhas conforme necessário. Lembre-se: existem **10 regras escondidas** no código!

## Exemplo de linha bem preenchida

| ID     | Regra de Negócio                                                                        | Programa Fonte                                   | Campos DDM                                                               | Nível de Risco | Notas                                      |
| ------ | --------------------------------------------------------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------------ | -------------- | ------------------------------------------ |
| BR-013 | Desconto total não pode exceder 30% do valor bruto, exceto descontos judiciais (tipo J) | `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L142-L148` | `PAGAMENTO.VLR-BRUTO`, `PAGAMENTO.VLR-TOTAL-DSCT`, `PAGAMENTO.TIPO-DSCT` | CRÍTICO        | Regra financeira. Tipo 'J' = exceção legal |

## Regras por Categoria

### Cálculos Financeiros

- BR-008 (correcao por IPCA)
- BR-009 (teto de descontos)

### Validações de Status

- BR-001 (status de pagamento por retorno bancario)
- BR-004 (status de idoso)
- BR-007 (bloqueio para nao ativos)
- BR-013 (dominio valido de status)

### Regras de Autorização

- BR-011 (filtros de auditoria para consulta de eventos)

### Regras de Negócio Temporais

- BR-002 (geracao por lote mensal com filtros de status)
- BR-012 (filtro por programa no periodo do relatorio)

## Resumo Estatístico

- Total de regras encontradas: 15
- Regras críticas: 2
- Regras com duplicação: 1 (controle de status em BATCHPGT, CALCBENF e VALBENEF)
- Regras sem documentação (escondidas): 3 (regiao 99, teto 30%, excecao tipo judicial)

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 1</strong></a><br/>
<sub>Passo a passo do estágio.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="dependency-map.md"><strong>dependency-map.md</strong></a><br/>
<sub>Mapa de quem chama quem.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

