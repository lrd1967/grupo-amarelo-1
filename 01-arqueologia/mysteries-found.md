<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Mistérios Encontrados — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **mysteries-found**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui toda lógica, comportamento ou código que o time não conseguiu explicar.
> "Mistérios" são trechos de código sem documentação, com lógica não-óbvia ou que parecem workarounds.
>
> **Cota mínima para passar pelo portão do Estágio 2:** 5 mistérios documentados.

## O que conta como "mistério"?

- Código que faz algo inesperado sem comentário explicando por quê
- Valores hardcoded sem explicação (números mágicos)
- Lógica condicional que parece um workaround ou gambiarra
- Campos no DDM que não são usados por nenhum programa
- Programas que existem mas não são chamados por ninguém
- Comportamento diferente entre o que a documentação diz e o que o código faz
- Easter eggs deixados pelos desenvolvedores originais

## Níveis de Confiança

| Nível     | Significado                                         |
| --------- | --------------------------------------------------- |
| **ALTA**  | Temos certeza de que há algo estranho aqui          |
| **MÉDIA** | Parece suspeito, mas pode ter explicação            |
| **BAIXA** | Pode ser intencional, mas não conseguimos confirmar |

## Mistérios Catalogados

| ID      | Descrição | Onde Encontrado | Impacto Potencial | Confiança |
| ------- | --------- | --------------- | ----------------- | --------- |
| MYS-001 | BATCHPGT declara que chama CALCBENF/CALCDSCT, mas executa cálculo simplificado local sem chamada externa. | BATCHPGT.NSN (linhas 14, 262, 306) | Divergência de valor entre lote e rotinas oficiais, quebrando consistência de pagamentos e reconciliação. | ALTA |
| MYS-002 | Comentário do 13º em CALCBENF usa meses ativos, mas implementação calcula com fator de idade. | CALCBENF.NSN (linhas 240, 244) | Pagamento anual potencialmente incorreto, com risco financeiro e regulatório. | ALTA |
| MYS-003 | CALCCORR parece aplicar apenas um índice mensal por registro, apesar de objetivo de correção retroativa por período. | CALCCORR.NSN (linhas 8, 147, 185) | Retroativos podem ser sub/supercorrigidos, afetando acerto histórico e auditoria. | MÉDIA |
| MYS-004 | RELAUDIT exclui eventos de ação EX da trilha exibida. | RELAUDIT.NSN (linhas 103, 105) | Perda de rastreabilidade de exclusões, prejudicando investigação e compliance. | ALTA |
| MYS-005 | CONSBENF afirma mascarar CPF, mas mantém comportamento conhecido que pode expor os primeiros dígitos. | CONSBENF.NSN (linhas 105, 172, 179) | Vazamento parcial de dado sensível e inconsistência na proteção de CPF. | ALTA |
| MYS-006 | CONSBENF promete últimos 12 pagamentos, mas leitura sem ordenação pode não trazer os mais recentes. | CONSBENF.NSN (linhas 145, 151, 156) | Operação pode consultar histórico desatualizado e tomar decisões erradas. | MÉDIA |
| MYS-007 | VALDOCS valida CPF/RG, mas prefixo especial de CPF força resultado válido e zera erros. | VALDOCS.NSN (linhas 68, 78, 177, 178) | Aprovação indevida de documentação inválida, abrindo risco de fraude cadastral. | ALTA |
| MYS-008 | VALELEG aprova automaticamente beneficiário de região 99 e sai da rotina antes das demais regras. | VALELEG.NSN (linhas 105, 108, 110) | Concessão sem validações de status/renda/idade/documentos. | ALTA |
| MYS-009 | CADDEPEND usa limite > 5 e depois incrementa contador, sugerindo possível inclusão de 6º dependente. | CADDEPEND.NSN (linhas 63, 111) | Off-by-one pode romper regras de negócio e estruturas que assumem máximo de 5. | MÉDIA |
| MYS-010 | BATCHREL declara sumarização por região/programa/status, mas só imprime por região e status. | BATCHREL.NSN (linhas 9, 174, 184) | Falta de visão por programa social em relatórios gerenciais. | ALTA |

## Detalhamento dos Mistérios

### MYS-001: Divergência entre objetivo declarado e implementação do batch

- **Arquivo:** BATCHPGT.NSN:10-18, BATCHPGT.NSN:258-314
- **Trecho de código:**

```natural
PERFORM DET-FAIXA-RENDA-BATCH

CALC DESCONTOS SIMPLIFICADO
IF #VLR-BRUTO > 500.00
COMPUTE #VLR-DESC = #VLR-BRUTO * 0.03
```

- **O que esperávamos:** o batch chamar os módulos de cálculo e desconto dedicados, conforme comentário do cabeçalho.
- **O que o código faz:** recalcula tudo internamente com regras simplificadas.
- **Hipótese do time:** houve substituição local por performance/urgência sem atualização da documentação.
- **Risco se ignorarmos:** pagamentos diferentes entre canais (online x batch), gerando inconsistência contábil e retrabalho de reconciliação.

### MYS-002: Fórmula do 13º no comentário não bate com a fórmula executada

- **Arquivo:** CALCBENF.NSN:236-251
- **Trecho de código:**

```natural
FORMULA DIFERENCIADA: VLR_13 = VLR_BASE * FATOR_REG * (MESES_ATIVOS/12)
COMPUTE #VLR-13 = #VLR-BASE * #FATOR-REG * #FATOR-IDADE
```

- **O que esperávamos:** cálculo proporcional por meses ativos.
- **O que o código faz:** usa fator de idade no lugar da proporcionalidade mensal.
- **Hipótese do time:** regra de negócio mudou e o comentário ficou legado.
- **Risco se ignorarmos:** erro recorrente de 13º, com potencial passivo financeiro e questionamento de auditoria.

### MYS-003: Correção retroativa aparentemente não acumula mês a mês no período

- **Arquivo:** CALCCORR.NSN:140-190
- **Trecho de código:**

```natural
MOVE PAGAMENTO-V.COMPETENCIA TO #COMP-ATUAL
PERFORM CALC-INDICE-ACUM
COMPUTE #IND-ACUM = #IND-ACUM * (1 + #IPCA-ANO(#K,#MES-C))
```

- **O que esperávamos:** laço de competências do intervalo informado, acumulando todos os meses.
- **O que o código faz:** calcula índice com base na competência do registro corrente.
- **Hipótese do time:** simplificação para reduzir custo de processamento.
- **Risco se ignorarmos:** retroativos sub ou supercorrigidos, comprometendo acertos históricos.

### MYS-004: Relatório de auditoria oculta exclusões

- **Arquivo:** RELAUDIT.NSN:99-112
- **Trecho de código:**

```natural
FILTRO ACAO - EXCLUSOES NAO SAO EXIBIDAS
IF AUDITORIA-V.ACAO = 'EX'
ADD 1 TO #QTD-FILTRADOS
ESCAPE TOP
```

- **O que esperávamos:** trilha completa, incluindo exclusões.
- **O que o código faz:** remove explicitamente ações EX da saída.
- **Hipótese do time:** decisão antiga para reduzir ruído no relatório operacional.
- **Risco se ignorarmos:** perda de evidência em investigação e fragilidade de compliance.

### MYS-005: Máscara de CPF com comportamento contraditório e potencial vazamento

- **Arquivo:** CONSBENF.NSN:168-193
- **Trecho de código:**

```natural
NOTA: INCONSISTENCIA CONHECIDA - AS VEZES MOSTRA PRIMEIROS 3
IF BENEFICIARIO-V.CPF < 10000000000
MOVE SUBSTR(#CPF-STR,1,3) TO #CPF-P1
COMPRESS #CPF-P1 '..' '-**' INTO #CPF-MASK
```

- **O que esperávamos:** máscara uniforme que nunca exponha parte inicial sensível.
- **O que o código faz:** em um caminho, exibe os 3 primeiros dígitos.
- **Hipótese do time:** workaround para CPFs com preenchimento à esquerda.
- **Risco se ignorarmos:** exposição parcial de PII e inconsistência de privacidade.

### MYS-006: Histórico diz últimos 12, mas sem ordenação de recência

- **Arquivo:** CONSBENF.NSN:143-160
- **Trecho de código:**

```natural
'HISTORICO PAGAMENTOS (ULTIMOS 12)'
...
READ PAGAMENTO-V BY CPF-BENEF = BENEFICIARIO-V.CPF
...
IF #QTD-HIST > 12
ESCAPE BOTTOM
```

- **O que esperávamos:** consulta ordenada pelos mais recentes e limitação em 12.
- **O que o código faz:** limita a 12 sem garantir ordem temporal decrescente.
- **Hipótese do time:** premissa antiga de ordenação física dos dados.
- **Risco se ignorarmos:** visão operacional desatualizada durante atendimento/análise.

### MYS-007: Prefixo especial invalida o resultado de validações anteriores

- **Arquivo:** VALDOCS.NSN:164-181
- **Trecho de código:**

```natural
IF #PREF-CPF = #PREF-ESP(#I)
MOVE TRUE TO #DOC-ESP-OK
MOVE TRUE TO #CPF-OK
MOVE 'V' TO #RESULTADO
MOVE 0 TO #QTD-ERROS
```

- **O que esperávamos:** exceção controlada sem apagar erros críticos já detectados.
- **O que o código faz:** força sucesso global e zera contador de erros.
- **Hipótese do time:** bypass de ambiente de teste que ficou em produção.
- **Risco se ignorarmos:** aceitação de documentos inválidos e aumento de fraude cadastral.

### MYS-008: Região 99 contorna todas as demais regras de elegibilidade

- **Arquivo:** VALELEG.NSN:101-112
- **Trecho de código:**

```natural
REGIAO 99 - INTERNACIONAL/DIPLOMATICO
IF #COD-REG = 99
MOVE TRUE TO #ELEGIVEL
WRITE 'BENEFICIARIO ELEGIVEL - REGIAO ESPECIAL'
ESCAPE ROUTINE
```

- **O que esperávamos:** região especial com regras próprias, mas ainda auditáveis.
- **O que o código faz:** aprova e encerra imediatamente a rotina.
- **Hipótese do time:** regra emergencial para um convênio específico.
- **Risco se ignorarmos:** concessões indevidas sem validação de status, renda, idade e documentação.

### MYS-009: Possível off-by-one no limite de dependentes

- **Arquivo:** CADDEPEND.NSN:60-114
- **Trecho de código:**

```natural
IF #NUM-DEP > 5
WRITE 'LIMITE DE DEPENDENTES ATINGIDO'
ESCAPE BOTTOM
...
ADD 1 TO #NUM-DEP
MOVE #NUM-DEP TO #IDX
```

- **O que esperávamos:** bloqueio ao atingir 5, antes de qualquer incremento.
- **O que o código faz:** só bloqueia quando já passou de 5.
- **Hipótese do time:** interpretação ambígua entre máximo permitido e índice interno.
- **Risco se ignorarmos:** gravação além do limite de negócio e inconsistência em cálculos dependentes.

### MYS-010: Objetivo promete sumarização por programa, mas saída não mostra programa

- **Arquivo:** BATCHREL.NSN:6-12, BATCHREL.NSN:172-191
- **Trecho de código:**

```natural
SUMARIZACAO POR REGIAO/PROGRAMA/STATUS
...
WRITE 'RESUMO POR REGIAO'
...
WRITE 'RESUMO POR STATUS'
```

- **O que esperávamos:** bloco de consolidação explícito por programa.
- **O que o código faz:** imprime apenas por região e por status.
- **Hipótese do time:** seção por programa removida em manutenção sem atualizar cabeçalho.
- **Risco se ignorarmos:** visão gerencial incompleta de custo/volume por programa social.

## Easter Eggs

> Dica: existem **3 easter eggs** escondidos no código legado. Registre aqui os que encontrar:

1. [x] Easter Egg 1: Bloco comentado em CALCCORR referencia a política econômica Plano Verão (correção 01/1989-01/1991, transição Cruzado->Cruzeiro).
2. [x] Easter Egg 2: VALDOCS.NSN (subrotina CHECK-DOC-ESPECIAL) aceita documentos por prefixos especiais (governo/teste) e força resultado válido sem validação completa.
3. [x] Easter Egg 3: Código morto em BATCHCON referencia integração descontinuada com o Banco Real (empresa incorporada pelo Santander).

## Bônus

1. INC-001 - limite documentado diverge do que o código permite.
O manual técnico diz que o campo BN-QT-DEPEND aceita valores de 0 a 3 e que o máximo por titular é 3 dependentes (MANUAL-TECNICO-SIFAP-2008.md:243, MANUAL-TECNICO-SIFAP-2008.md:261). No código, porém, o cadastro de dependentes só bloqueia quando #NUM-DEP > 5 (CADDEPEND.NSN:63). Na prática, o sistema passou a aceitar até 5 dependentes, então a documentação ficou defasada.
1. INC-002 - a arquitetura original não menciona uma estrutura de dados que entrou depois.
O documento de arquitetura original lista apenas 3 DDMs principais - BENEFICIARIO, PROGRAMA-SOCIAL e PAGAMENTO (ARQUITETURA-ORIGINAL-1997.md:162). O próprio documento admite, em nota, que o DDM AUDITORIA (FNR 153) foi adicionado depois, em 2005, e não fazia parte da redação original (ARQUITETURA-ORIGINAL-1997.md:207, ARQUITETURA-ORIGINAL-1997.md:210). O código confirma a existência dessa estrutura, por exemplo em AUDITORIA-V VIEW OF AUDITORIA no BATCHCON (BATCHCON.NSN:28). Isso é menos uma contradição e mais um caso clássico de evolução não refletida na arquitetura inicial.
1. INC-003 - regras críticas de cálculo não aparecem de forma operacional nos documentos.
O levantamento parcial de regras admite explicitamente que o cálculo do 13o benefício, o fator K, o pro rata e o bypass por região 99 não foram totalmente documentados (REGRAS-NEGOCIO-2012.md:245, REGRAS-NEGOCIO-2012.md:250, REGRAS-NEGOCIO-2012.md:290). O manual técnico também deixa o cálculo do 13o como “a completar” (MANUAL-TECNICO-SIFAP-2008.md:298). Já o código implementa regra concreta e crítica: cálculo do benefício com múltiplos fatores, truncamento em centavos, 13o em dezembro e abono natalino de 15% para programa tipo A (CALCBENF.NSN:231, CALCBENF.NSN:239, CALCBENF.NSN:250). Para correção monetária, o código ainda mantém a série mensal de IPCA e a lógica de acumulação, que não aparece descrita operacionalmente nos docs (CALCCORR.NSN:57, CALCCORR.NSN:185). Aqui a inconsistência é principalmente documental, mas afeta diretamente valores pagos.
1. INC-004 - dois programas usam métodos diferentes de arredondamento para o mesmo tipo de valor.
A regra documental diz que o valor do benefício deve ser sempre arredondado para baixo em centavos, isto é, truncado (REGRAS-NEGOCIO-2012.md:126). O CALCBENF segue esse comportamento com truncamento puro (* 100 e depois / 100) (CALCBENF.NSN:231, CALCBENF.NSN:233). Já o BATCHREL faz algo diferente: soma 0.005 antes de truncar e ainda registra que o arredondamento difere do CALCBENF (BATCHREL.NSN:136, BATCHREL.NSN:137). Isso pode gerar diferença de 1 centavo entre cálculo e relatório para o mesmo valor monetário.
 

## Resumo

- Total de mistérios encontrados: 10
- Confiança alta: 7
- Confiança média: 3
- Confiança baixa: 0
- Easter eggs encontrados: 3 / 3

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-checklist.md"><strong>mysteries-checklist.md</strong></a><br/>
<sub>Lista do que procurar.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="discovery-report.md"><strong>discovery-report.md</strong></a><br/>
<sub>Síntese final.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

