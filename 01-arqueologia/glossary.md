<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Glossário do SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **glossary**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Preencha esta tabela com todos os termos, abreviações e siglas encontrados no código Natural/Adabas.
> **Meta: no mínimo 30 termos.**

## Por que isso importa

Sistemas legados têm vocabulário próprio que ninguém documenta em lugar nenhum — só está no nome das variáveis. Se o time do Estágio 2 não souber o que `DSCT`, `BENF`, `PE` ou `CTC` significam, vai escrever uma spec sobre o que ele _acha_ que isso significa. Glossário é o que evita esse desencontro.

## Como preencher

- **Termo**: a abreviação ou sigla exatamente como aparece no código
- **Expansão**: o significado completo do termo
- **Programa**: em qual arquivo `.NSN` ou `.ddm` o termo foi encontrado
- **Contexto**: breve explicação de como/onde o termo é usado

## Dica de extração

Prompt útil no Copilot Chat (cole o conteúdo de 2–3 arquivos `.NSN` no chat antes):

> _"Liste todas as abreviações e siglas usadas neste código Natural. Para cada uma, sugira a expansão e marque com 'CONFIRMADO' ou 'HIPÓTESE'."_

## Termos encontrados

| #   | Termo | Expansão | Programa | Contexto |
| --- | ----- | -------- | -------- | -------- |
| 1   | `CPF` | Cadastro de Pessoa Fisica | `CADBENEF.NSN`, `CONSBENF.NSN`, `VALBENEF.NSN`, `VALDOCS.NSN` | Identificador principal do beneficiario; validacao modulo 11. |
| 2   | `NIS` | Numero de Identificacao Social | `CADBENEF.NSN`, `CONSBENF.NSN`, `VALELEG.NSN` | Chave de elegibilidade; em regra `R` nao pode ser zero. |
| 3   | `STATUS` | Situacao cadastral do beneficiario | `CADBENEF.NSN`, `CONSBENF.NSN`, `VALBENEF.NSN`, `VALELEG.NSN` | Dominio: `A,S,C,I,D` (ativo, suspenso, cancelado, inativo, desligado). |
| 4   | `COD-PROGRAMA` | Codigo do programa social | `BATCHPGT.NSN`, `CADPROG.NSN`, `RELPGT.NSN`, `VALELEG.NSN` | Chave numerica do programa vinculado ao beneficiario/pagamento. |
| 5   | `NOME` | Nome do beneficiario | `CADBENEF.NSN`, `CONSBENF.NSN` | Nome civil exibido em consulta e relatorios. |
| 6   | `DT-NASCIMENTO` | Data de nascimento | `CADBENEF.NSN`, `CONSBENF.NSN`, `CALCBENF.NSN` | Base para idade e regras de elegibilidade. |
| 7   | `SEXO` | Sexo cadastral | `CADBENEF.NSN`, `CADDEPEND.NSN` | Campo de caracter unico em beneficiario/dependente. |
| 8   | `UF` | Unidade Federativa | `CADBENEF.NSN`, `CONSBENF.NSN`, `VALBENEF.NSN` | Sigla do estado; validada contra tabela de 27 UFs. |
| 9   | `CEP` | Codigo de Enderecamento Postal | `CADBENEF.NSN`, `CONSBENF.NSN` | CEP residencial do beneficiario. |
| 10  | `RG` | Registro Geral | `CADBENEF.NSN`, `VALDOCS.NSN` | Documento de identificacao civil. |
| 11  | `RENDA-FAMILIAR` | Renda familiar informada | `CADBENEF.NSN`, `CALCBENF.NSN`, `VALELEG.NSN` | Usada em calculo do beneficio e criterio de elegibilidade. |
| 12  | `NUM-DEPENDENTES` | Quantidade de dependentes | `CADBENEF.NSN`, `CADDEPEND.NSN`, `VALELEG.NSN` | Contador de dependentes vinculados ao titular. |
| 13  | `DEPENDENTES` | Grupo periodico de dependentes | `CADDEPEND.NSN` | Estrutura `PE` com repeticao de dados de dependente. |
| 14  | `PARENTESCO` | Relacao com titular | `CADDEPEND.NSN` | Dominio: `FI` filho, `CO` conjuge, `IR` irmao. |
| 15  | `CPF-DEP` | CPF do dependente | `CADDEPEND.NSN` | CPF do membro dependente no grupo repetitivo. |
| 16  | `COD-REGIAO` | Codigo geografico de regiao | `CADBENEF.NSN`, `CALCBENF.NSN`, `BATCHREL.NSN` | Influencia fator regional de calculo e sumarizacao. |
| 17  | `TIPO` | Tipo do programa | `CADPROG.NSN`, `CALCBENF.NSN`, `VALELEG.NSN` | Dominio: `A` assistencial, `P` previdenciario, `T` trabalho. |
| 18  | `COD-ELEGIBILIDADE` | Codigo de regras especificas | `CADPROG.NSN`, `VALELEG.NSN` | `R` exige NIS valido; `D` exige dependentes. |
| 19  | `STATUS-PROG` | Situacao do programa social | `CADPROG.NSN`, `BATCHPGT.NSN`, `VALELEG.NSN` | `A` ativo (uso explicito para liberacao de processamento). |
| 20  | `NUM-PAGTO` | Numero do pagamento | `BATCHCON.NSN`, `CALCDSCT.NSN`, `RELPGT.NSN` | Identificador da parcela gerada no ciclo. |
| 21  | `COMPETENCIA` | Referencia ano/mes | `CALCBENF.NSN`, `BATCHPGT.NSN`, `RELPGT.NSN` | Formato `AAAAMM` para processamento mensal. |
| 22  | `VLR-BRUTO` | Valor bruto do beneficio | `CALCBENF.NSN`, `BATCHREL.NSN`, `RELPGT.NSN` | Base para calculo de descontos e relatorios. |
| 23  | `VLR-DESCONTO` | Total de descontos aplicados | `CALCBENF.NSN`, `CALCDSCT.NSN`, `RELPGT.NSN` | Teto de 30% do bruto, exceto desconto judicial. |
| 24  | `VLR-LIQUIDO` | Valor liquido a pagar | `BATCHCON.NSN`, `CALCBENF.NSN`, `RELPGT.NSN` | Resultado final apos descontos e ajustes. |
| 25  | `TIPO-PGTO` | Tipo de pagamento | `CALCBENF.NSN`, `BATCHPGT.NSN`, `RELPGT.NSN` | Dominio: `N` normal, `D` decimo, `T` terceiro. |
| 26  | `STATUS-PGTO` | Status operacional do pagamento | `BATCHCON.NSN`, `BATCHREL.NSN`, `RELPGT.NSN` | Dominio: `G` gerado, `P` pago, `C` cancelado, `D` devolvido, `E` estornado. |
| 27  | `COD-RETORNO` | Codigo de retorno bancario CNAB | `BATCHCON.NSN` | Dominio: `00` pago, `01` devolvido, `02` estornado. |
| 28  | `TIPO-DSCT` | Tipo de desconto cadastrado | `CALCDSCT.NSN` | Dominio: `C` contribuicao, `I` imposto, `J` judicial, `S` sindical, `P` pensao, `A` administrativo. |
| 29  | `ACAO` | Tipo de evento de auditoria | `BATCHCON.NSN`, `RELAUDIT.NSN` | Dominio: `IN`, `AL`, `CO`, `CN`, `DV`. |
| 30  | `TIPO-BUSCA` | Modo de consulta do beneficiario | `CONSBENF.NSN` | Dominio: `C` por CPF, `N` por NIS. |

> Adicione mais linhas conforme necessário. Não se limite a 30!

## Exemplo de linha bem preenchida

| #   | Termo  | Expansão | Programa                        | Contexto                                                                                                         |
| --- | ------ | -------- | ------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| 1   | `DSCT` | Desconto | `CALCDSCT.NSN`, `PAGAMENTO.ddm` | Tipo de dedução aplicada sobre valor bruto do pagamento. Tipos: 'J' (judicial), 'I' (imposto), 'T' (trabalhista) |

## Observações

- Anote aqui qualquer padrão de nomenclatura que o time identificou:
- Convenções de prefixo/sufixo encontradas:
- Termos ambíguos que precisam de validação com especialista:

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
<a href="business-rules-catalog.md"><strong>business-rules-catalog.md</strong></a><br/>
<sub>Catálogo de regras.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

