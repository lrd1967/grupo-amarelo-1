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
| BR-001 | Competência de cálculo deve ter mês entre 1 e 12; fora disso o processamento é encerrado. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L141-L144 | PAGAMENTO.COMPETENCIA | ALTO | Evita geração de pagamento em período inválido. |
| BR-002 | Apenas beneficiário com status A pode ter benefício calculado. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L160-L163 | BENEFICIARIO.STATUS | CRÍTICO | Bloqueia cálculo para cadastro suspenso/inativo. |
| BR-003 | Fator regional usa tabela para códigos 1..25; fora da faixa aplica fator neutro 1.0000. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L180-L184 | BENEFICIARIO.COD-REGIAO | ALTO | Regra altera diretamente o valor do benefício. |
| BR-004 | Fator familiar é progressivo por dependentes: até 2, até 4 e acima de 4 com fórmulas distintas. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L187-L197 | BENEFICIARIO.NUM-DEPENDENTES | ALTO | Curva não linear de composição do benefício. |
| BR-005 | Fator de idade: 65+ = 1.15; 60-64 = 1.10; menor de 18 = 1.05; demais = 1.00. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L207-L217 | BENEFICIARIO.DT-NASCIMENTO | ALTO | Impacto direto no valor pago. |
| BR-006 | Em dezembro, pagamento vira tipo D e inclui parcela de 13o calculada por base regional e idade. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L242-L250 | PAGAMENTO.TIPO-PGTO, PAGAMENTO.VLR-BRUTO | CRÍTICO | Regra financeira de fim de ano. |
| BR-007 | Programa do tipo A recebe abono natalino de 15% em dezembro. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L251-L257 | PROGRAMA-SOCIAL.TIPO, PAGAMENTO.VLR-ABONO | CRÍTICO | Exceção por tipo de programa. |
| BR-008 | Desconto básico simplificado de 3% só ocorre quando valor bruto é maior que 500.00. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L318-L322 | PAGAMENTO.VLR-BRUTO, PAGAMENTO.VLR-DESCONTO | ALTO | Regra de contribuição social simplificada. |
| BR-009 | Valor líquido não pode ficar negativo; mínimo operacional é zero. | 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L267-L269 | PAGAMENTO.VLR-LIQUIDO | CRÍTICO | Evita pagamento negativo. |
| BR-010 | Contribuição social obrigatória usa faixas fixas (500/1000/2000/9999.99) com alíquotas 3/5/7/9%. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L65-L72 | PAGAMENTO.VLR-BRUTO, BENEFICIARIO.DESCONTOS | CRÍTICO | Tabela explícita de alíquotas. |
| BR-011 | Total de descontos não judiciais tem teto de 30% do valor bruto, com truncamento para 2 casas. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L102-L106 | PAGAMENTO.VLR-BRUTO, PAGAMENTO.VLR-DESCONTO | CRÍTICO | Limite financeiro central do domínio. |
| BR-012 | Desconto expirado (DT-FIM menor que hoje) ou ainda não iniciado não é aplicado. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L112-L118 | BENEFICIARIO.DESCONTOS.DT-INICIO-DSCT, BENEFICIARIO.DESCONTOS.DT-FIM-DSCT | ALTO | Vigência temporal por item de desconto. |
| BR-013 | Desconto judicial (tipo J) pode ser valor fixo ou percentual e não sofre teto de 30%. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L123-L134 | BENEFICIARIO.DESCONTOS.TIPO-DSCT, PAGAMENTO.VLR-DESCONTO | CRÍTICO | Exceção legal explícita. |
| BR-014 | Desconto sindical (tipo S) é 1% fixo do valor bruto. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L147-L150 | BENEFICIARIO.DESCONTOS.TIPO-DSCT, PAGAMENTO.VLR-DESCONTO | ALTO | Percentual fixo hardcoded. |
| BR-015 | Após calcular descontos, o valor é persistido no pagamento do número informado. | 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L179-L183 | PAGAMENTO.NUM-PAGTO, PAGAMENTO.VLR-DESCONTO | ALTO | Atualização transacional da folha. |
| BR-016 | Se competência inicial for maior que final, correção retroativa é abortada. | 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L119-L122 | PAGAMENTO.COMPETENCIA | ALTO | Proteção de faixa temporal. |
| BR-017 | Pagamento com IND-CORRIGIDO = S é ignorado para evitar correção dupla. | 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L140-L142 | PAGAMENTO.IND-CORRIGIDO | ALTO | Idempotência do recálculo. |
| BR-018 | Correção só é gravada quando diferença calculada é positiva; marca IND-CORRIGIDO = S. | 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L158-L163 | PAGAMENTO.VLR-CORRECAO, PAGAMENTO.IND-CORRIGIDO | CRÍTICO | Impede redução automática de valores. |
| BR-019 | Beneficiário em região 99 é automaticamente elegível, com saída imediata da rotina. | 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L107-L110 | BENEFICIARIO.COD-REGIAO | ALTO | Regra excepcional de elegibilidade. |
| BR-020 | Programa assistencial (tipo A): se renda > 600 e sem dependentes, torna não elegível; também exige DOCUMENTOS-OK = S. | 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L169-L181 | PROGRAMA-SOCIAL.TIPO, BENEFICIARIO.RENDA-FAMILIAR, BENEFICIARIO.NUM-DEPENDENTES, BENEFICIARIO.DOCUMENTOS-OK | CRÍTICO | Combina renda, composição familiar e documentação. |
| BR-021 | Programa previdenciário (tipo P) exige idade mínima de 60 anos. | 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L183-L188 | PROGRAMA-SOCIAL.TIPO, BENEFICIARIO.DT-NASCIMENTO | ALTO | Gate etário específico. |
| BR-022 | Programa de trabalho (tipo T) exige faixa etária entre 16 e 65 anos. | 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L190-L195 | PROGRAMA-SOCIAL.TIPO, BENEFICIARIO.DT-NASCIMENTO | ALTO | Regra etária bidirecional. |
| BR-023 | Códigos de elegibilidade especiais podem exigir NIS cadastrado e/ou presença de dependentes. | 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L228-L240 | BENEFICIARIO.NIS, BENEFICIARIO.NUM-DEPENDENTES, PROGRAMA-SOCIAL.COD-ELEGIBILIDADE | ALTO | Flags R/D em COD-ELEGIBILIDADE ativam validações extras. |
| BR-024 | Cadastro de beneficiário: operação só pode ser I (inclusão) ou A (alteração). | 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L99-L103 | BENEFICIARIO.CPF | MÉDIO | Validação de comando de negócio. |
| BR-025 | Na inclusão, status inicial é A; para idade acima de 75, status é ajustado para S. | 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L162-L168 | BENEFICIARIO.STATUS, BENEFICIARIO.DT-NASCIMENTO | ALTO | Regra de onboarding com exceção etária. |
| BR-026 | Validação de CPF aceita exceção de teste para CPFs iniciados com 000. | 01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L196-L201 | BENEFICIARIO.CPF | MÉDIO | Exceção explícita de governo/teste. |
| BR-027 | Status cadastral válido é apenas A, S, C, I ou D. | 01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L164-L168 | BENEFICIARIO.STATUS | ALTO | Lista fechada de estados permitidos. |
| BR-028 | Documento especial por prefixo de CPF (inclui 999) sobrescreve erros e força resultado válido. | 01-arqueologia/legado-sifap/natural-programs/VALDOCS.NSN#L56-L57 | BENEFICIARIO.CPF, BENEFICIARIO.DOCUMENTOS-OK | ALTO | Bypass de validação documental padrão. |
| BR-029 | BATCHPGT ignora registros duplicados consecutivos de CPF para evitar pagamento em duplicidade. | 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L188-L192 | BENEFICIARIO.CPF | CRÍTICO | Contenção de duplicidade em lote mensal. |
| BR-030 | BATCHPGT não gera pagamento para beneficiário não ativo, programa inativo ou já pago na competência. | 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L195-L205 | BENEFICIARIO.STATUS, PROGRAMA-SOCIAL.STATUS-PROG, PAGAMENTO.COMPETENCIA | CRÍTICO | Três gates de bloqueio antes da geração. |

> Adicione mais linhas conforme necessário. Lembre-se: existem **10 regras escondidas** no código!

## Exemplo de linha bem preenchida

| ID     | Regra de Negócio                                                                        | Programa Fonte                                   | Campos DDM                                                               | Nível de Risco | Notas                                      |
| ------ | --------------------------------------------------------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------------ | -------------- | ------------------------------------------ |
| BR-013 | Desconto total não pode exceder 30% do valor bruto, exceto descontos judiciais (tipo J) | `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L142-L148` | `PAGAMENTO.VLR-BRUTO`, `PAGAMENTO.VLR-TOTAL-DSCT`, `PAGAMENTO.TIPO-DSCT` | CRÍTICO        | Regra financeira. Tipo 'J' = exceção legal |

## Regras por Categoria

### Cálculos Financeiros

- BR-003, BR-004, BR-005, BR-006, BR-007, BR-008, BR-009, BR-010, BR-011, BR-013, BR-014, BR-018.

### Validações de Status

- BR-002, BR-017, BR-025, BR-027, BR-030.

### Regras de Autorização

- BR-019, BR-020, BR-021, BR-022, BR-023, BR-028.

### Regras de Negócio Temporais

- BR-001, BR-006, BR-012, BR-016, BR-030.

## Resumo Estatístico

- Total de regras encontradas: 30
- Regras críticas: 11
- Regras com duplicação: 4
- Regras sem documentação (escondidas): 10

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

