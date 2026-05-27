<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# SPECIFICATION — SIFAP 2.0 · Etapa 1 (EARS)

![ESTÁGIO 02](https://img.shields.io/badge/EST%C3%81GIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![EARS](https://img.shields.io/badge/FORMATO-EARS-1A1A1A?style=for-the-badge) ![Rastreabilidade](https://img.shields.io/badge/source__legacy-Obrigat%C3%B3rio-737373?style=for-the-badge)

## Metadados

- **Versão da spec:** 1.0.0
- **Time:** Grupo Amarelo 1
- **Data:** 27/05/2026
- **Origem dos requisitos:** `01-arqueologia/business-rules-catalog.md` (BR-001 a BR-030)
- **Objetivo desta etapa:** escrever requisitos EARS testáveis com `source_legacy:` obrigatório

---

## 1. Escopo desta especificação

Esta especificação cobre as regras funcionais essenciais para modernização do SIFAP nos domínios:

- Cadastro e validação de beneficiários
- Elegibilidade
- Cálculo de benefícios e descontos
- Correção retroativa
- Processamento batch de pagamentos

Itens estruturais (ADRs e C4) foram entregues nos arquivos complementares:

- `02-spec-moderna/ADR-001-modular-monolith.md`
- `02-spec-moderna/ADR-002-migracao-dados.md`
- `02-spec-moderna/ADR-003-seguranca-autenticacao-autorizacao.md`
- `02-spec-moderna/ADR-004-estrategia-batch.md`
- `02-spec-moderna/C4-DIAGRAMS.md`

---

## 2. Requisitos EARS

### 2.1 Cadastro e validação

````yaml
REQ-CAD-001:
  pattern: unwanted
  text: "O SIFAP não deve aceitar operação de cadastro de beneficiário diferente de I (inclusão) ou A (alteração)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L99-L103
  business_rule: BR-024
  acceptance:
    - "Operação I é aceita."
    - "Operação A é aceita."
    - "Operação X retorna erro de validação."
  priority: P0
  risk: MÉDIO

REQ-CAD-002:
  pattern: event-driven
  text: "Quando um beneficiário é incluído, o SIFAP deve definir status inicial A e, para idade acima de 75 anos, ajustar o status para S."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L162-L168
  business_rule: BR-025
  acceptance:
    - "Novo beneficiário de 40 anos inicia com status A."
    - "Novo beneficiário de 76 anos inicia com status S."
  priority: P0
  risk: ALTO

REQ-VAL-001:
  pattern: event-driven
  text: "Quando o CPF do beneficiário é validado, o SIFAP deve aplicar a validação padrão e aceitar exceção para CPF iniciado por 000."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L196-L201
  business_rule: BR-026
  acceptance:
    - "CPF inválido comum é rejeitado."
    - "CPF com prefixo 000 é aceito pela exceção."
  priority: P1
  risk: MÉDIO

REQ-VAL-002:
  pattern: unwanted
  text: "O SIFAP não deve aceitar status cadastral fora do conjunto A, S, C, I ou D."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L164-L168
  business_rule: BR-027
  acceptance:
    - "Status A, S, C, I e D são aceitos."
    - "Status Z retorna erro de validação."
  priority: P0
  risk: ALTO

REQ-VAL-003:
  pattern: event-driven
  text: "Quando a validação documental identificar prefixo especial de CPF, incluindo 999, o SIFAP deve sobrescrever erros documentais e marcar a validação como positiva."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALDOCS.NSN#L56-L57
  business_rule: BR-028
  acceptance:
    - "CPF com prefixo especial e erro documental prévio resulta em validação final positiva."
    - "CPF sem prefixo especial mantém o resultado normal da validação documental."
  priority: P1
  risk: ALTO
````

### 2.2 Elegibilidade

````yaml
REQ-ELEG-001:
  pattern: event-driven
  text: "Quando a elegibilidade é avaliada para beneficiário da região 99, o SIFAP deve marcá-lo como elegível e encerrar a rotina de avaliação."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L107-L110
  business_rule: BR-019
  acceptance:
    - "Beneficiário com COD-REGIAO 99 retorna elegível sem validar outras regras."
  priority: P0
  risk: ALTO

REQ-ELEG-002:
  pattern: complex
  text: "Enquanto o programa social for do tipo A, quando a renda familiar for maior que 600 e não houver dependentes, o SIFAP deve marcar o beneficiário como não elegível; onde DOCUMENTOS-OK for diferente de S, também deve marcar como não elegível."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L169-L181
  business_rule: BR-020
  acceptance:
    - "Tipo A + renda 700 + 0 dependentes => não elegível."
    - "Tipo A + DOCUMENTOS-OK=N => não elegível."
    - "Tipo A + renda 550 + 2 dependentes + DOCUMENTOS-OK=S => permanece elegível pelas regras desta cláusula."
  priority: P0
  risk: CRÍTICO

REQ-ELEG-003:
  pattern: state-driven
  text: "Enquanto o programa social for do tipo P, o SIFAP deve exigir idade mínima de 60 anos para elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L183-L188
  business_rule: BR-021
  acceptance:
    - "Tipo P com 59 anos => não elegível."
    - "Tipo P com 60 anos => elegível."
  priority: P0
  risk: ALTO

REQ-ELEG-004:
  pattern: state-driven
  text: "Enquanto o programa social for do tipo T, o SIFAP deve exigir idade entre 16 e 65 anos, inclusive, para elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L190-L195
  business_rule: BR-022
  acceptance:
    - "Tipo T com 15 anos => não elegível."
    - "Tipo T com 16 anos => elegível."
    - "Tipo T com 66 anos => não elegível."
  priority: P0
  risk: ALTO

REQ-ELEG-005:
  pattern: complex
  text: "Quando códigos de elegibilidade especiais forem aplicados, onde houver flag R, o SIFAP deve exigir NIS cadastrado, e onde houver flag D, o SIFAP deve exigir pelo menos um dependente."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L228-L240
  business_rule: BR-023
  acceptance:
    - "Flag R sem NIS => não elegível."
    - "Flag D com 0 dependentes => não elegível."
    - "Flags R e D com NIS e dependente >=1 => elegível nesta regra."
  priority: P0
  risk: ALTO
````

### 2.3 Cálculo de benefício

````yaml
REQ-PAY-001:
  pattern: unwanted
  text: "O SIFAP não deve processar competência de cálculo com mês fora da faixa de 1 a 12."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L141-L144
  business_rule: BR-001
  acceptance:
    - "Competência mês 0 => processamento encerrado com erro."
    - "Competência mês 13 => processamento encerrado com erro."
    - "Competência mês 6 => processamento segue normalmente."
  priority: P0
  risk: ALTO

REQ-PAY-002:
  pattern: state-driven
  text: "Enquanto o beneficiário não estiver com status A, o SIFAP deve impedir o cálculo do benefício."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L160-L163
  business_rule: BR-002
  acceptance:
    - "Status A => cálculo permitido."
    - "Status S/I/C/D => cálculo bloqueado."
  priority: P0
  risk: CRÍTICO

REQ-PAY-003:
  pattern: event-driven
  text: "Quando o fator regional for calculado, o SIFAP deve usar tabela para códigos de região 1 a 25 e aplicar fator neutro 1.0000 fora dessa faixa."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L180-L184
  business_rule: BR-003
  acceptance:
    - "COD-REGIAO 10 aplica fator da tabela."
    - "COD-REGIAO 99 aplica fator 1.0000."
  priority: P0
  risk: ALTO

REQ-PAY-004:
  pattern: event-driven
  text: "Quando o fator familiar for calculado, o SIFAP deve aplicar fórmula progressiva para faixas de dependentes até 2, até 4 e acima de 4."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L187-L197
  business_rule: BR-004
  acceptance:
    - "Dependentes = 1 aplica fórmula da primeira faixa."
    - "Dependentes = 3 aplica fórmula da segunda faixa."
    - "Dependentes = 5 aplica fórmula da terceira faixa."
  priority: P0
  risk: ALTO

REQ-PAY-005:
  pattern: event-driven
  text: "Quando o fator etário for calculado, o SIFAP deve aplicar 1.15 para idade >= 65, 1.10 para 60 a 64, 1.05 para menor de 18 e 1.00 para demais idades."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L207-L217
  business_rule: BR-005
  acceptance:
    - "Idade 67 => fator 1.15."
    - "Idade 62 => fator 1.10."
    - "Idade 16 => fator 1.05."
    - "Idade 30 => fator 1.00."
  priority: P0
  risk: ALTO

REQ-PAY-006:
  pattern: complex
  text: "Quando a competência for dezembro, o SIFAP deve definir o pagamento como tipo D e incluir parcela de 13o calculada com base regional e etária."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L242-L250
  business_rule: BR-006
  acceptance:
    - "Competência dezembro => tipo D e valor de 13o calculado."
    - "Competência diferente de dezembro => não cria parcela de 13o por esta regra."
  priority: P0
  risk: CRÍTICO

REQ-PAY-007:
  pattern: complex
  text: "Enquanto a competência for dezembro, quando o programa social for tipo A, o SIFAP deve aplicar abono natalino de 15% ao pagamento."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L251-L257
  business_rule: BR-007
  acceptance:
    - "Tipo A em dezembro => abono de 15% aplicado."
    - "Tipo A fora de dezembro => sem abono por esta regra."
    - "Tipo diferente de A em dezembro => sem abono por esta regra."
  priority: P0
  risk: CRÍTICO
````

### 2.4 Descontos

````yaml
REQ-DSCT-001:
  pattern: state-driven
  text: "Enquanto o valor bruto do pagamento for maior que 500.00, o SIFAP deve aplicar desconto básico simplificado de 3%."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L318-L322
  business_rule: BR-008
  acceptance:
    - "Valor bruto 600 => desconto básico de 3%."
    - "Valor bruto 500 => sem desconto básico por esta regra."
  priority: P1
  risk: ALTO

REQ-DSCT-002:
  pattern: unwanted
  text: "O SIFAP não deve permitir valor líquido de pagamento negativo e deve aplicar valor mínimo operacional igual a zero."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L267-L269
  business_rule: BR-009
  acceptance:
    - "Se cálculo resultar em -10.00, valor líquido final é 0.00."
    - "Se cálculo resultar em 150.00, mantém 150.00."
  priority: P0
  risk: CRÍTICO

REQ-DSCT-003:
  pattern: event-driven
  text: "Quando a contribuição social obrigatória for calculada, o SIFAP deve aplicar alíquotas de 3%, 5%, 7% e 9% nas faixas 500, 1000, 2000 e 9999.99, respectivamente."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L65-L72
  business_rule: BR-010
  acceptance:
    - "Valor bruto na faixa até 500 aplica 3%."
    - "Valor bruto na faixa >500 e <=1000 aplica 5%."
    - "Valor bruto na faixa >1000 e <=2000 aplica 7%."
    - "Valor bruto acima de 2000 aplica 9%."
  priority: P0
  risk: CRÍTICO

REQ-DSCT-004:
  pattern: unwanted
  text: "O SIFAP não deve permitir que descontos não judiciais ultrapassem 30% do valor bruto e deve truncar o valor aplicado para 2 casas decimais."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L102-L106
  business_rule: BR-011
  acceptance:
    - "Descontos não judiciais de 35% são limitados a 30%."
    - "Valor final de desconto é truncado para 2 casas decimais."
  priority: P0
  risk: CRÍTICO

REQ-DSCT-005:
  pattern: state-driven
  text: "Enquanto um desconto estiver fora da vigência temporal (ainda não iniciado ou já expirado), o SIFAP não deve aplicá-lo ao pagamento."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L112-L118
  business_rule: BR-012
  acceptance:
    - "Desconto com DT-INICIO futura não é aplicado."
    - "Desconto com DT-FIM passada não é aplicado."
    - "Desconto vigente é aplicado."
  priority: P0
  risk: ALTO

REQ-DSCT-006:
  pattern: event-driven
  text: "Quando um desconto judicial (tipo J) for processado, o SIFAP deve aceitar valor fixo ou percentual e não aplicar teto de 30%."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L123-L134
  business_rule: BR-013
  acceptance:
    - "Desconto judicial fixo de 500 é aplicado integralmente."
    - "Desconto judicial percentual de 50% é aplicado integralmente."
  priority: P0
  risk: CRÍTICO

REQ-DSCT-007:
  pattern: ubiquitous
  text: "O SIFAP deve aplicar desconto sindical (tipo S) fixo de 1% sobre o valor bruto."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L147-L150
  business_rule: BR-014
  acceptance:
    - "Valor bruto 1000 com tipo S => desconto de 10."
  priority: P1
  risk: ALTO

REQ-DSCT-008:
  pattern: event-driven
  text: "Quando o cálculo de descontos for concluído, o SIFAP deve persistir o valor calculado no pagamento identificado pelo número informado."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L179-L183
  business_rule: BR-015
  acceptance:
    - "Ao finalizar cálculo, campo de desconto do pagamento é atualizado."
    - "Persistência ocorre no número de pagamento solicitado."
  priority: P0
  risk: ALTO
````

### 2.5 Correção retroativa

````yaml
REQ-CORR-001:
  pattern: unwanted
  text: "O SIFAP não deve iniciar correção retroativa quando a competência inicial for maior que a competência final."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L119-L122
  business_rule: BR-016
  acceptance:
    - "Competência inicial > final => processo abortado com erro."
  priority: P0
  risk: ALTO

REQ-CORR-002:
  pattern: state-driven
  text: "Enquanto o pagamento estiver marcado com IND-CORRIGIDO igual a S, o SIFAP deve ignorar nova tentativa de correção."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L140-L142
  business_rule: BR-017
  acceptance:
    - "Pagamento com IND-CORRIGIDO=S não recebe segunda correção."
  priority: P0
  risk: ALTO

REQ-CORR-003:
  pattern: event-driven
  text: "Quando a diferença de correção calculada for positiva, o SIFAP deve gravar o valor da correção e marcar IND-CORRIGIDO igual a S; quando não for positiva, não deve gravar correção."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L158-L163
  business_rule: BR-018
  acceptance:
    - "Diferença positiva => grava VLR-CORRECAO e IND-CORRIGIDO=S."
    - "Diferença zero/negativa => não grava correção."
  priority: P0
  risk: CRÍTICO
````

### 2.6 Processamento batch

````yaml
REQ-BATCH-001:
  pattern: event-driven
  text: "Quando o batch mensal encontrar registros consecutivos com CPF duplicado, o SIFAP deve ignorar o registro duplicado para evitar pagamento em duplicidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L188-L192
  business_rule: BR-029
  acceptance:
    - "Dois registros consecutivos com mesmo CPF geram apenas um pagamento."
  priority: P0
  risk: CRÍTICO

REQ-BATCH-002:
  pattern: complex
  text: "Enquanto a competência mensal estiver em processamento, quando o beneficiário não estiver ativo, onde o programa estiver inativo ou já existir pagamento na competência, o SIFAP não deve gerar novo pagamento."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L195-L205
  business_rule: BR-030
  acceptance:
    - "Beneficiário inativo => sem geração de pagamento."
    - "Programa inativo => sem geração de pagamento."
    - "Pagamento já existente na competência => sem duplicação."
  priority: P0
  risk: CRÍTICO
````

---

## 3. Matriz de rastreabilidade (BR → REQ)

| BR | REQ-ID | Status |
|---|---|---|
| BR-001 | REQ-PAY-001 | ✅ |
| BR-002 | REQ-PAY-002 | ✅ |
| BR-003 | REQ-PAY-003 | ✅ |
| BR-004 | REQ-PAY-004 | ✅ |
| BR-005 | REQ-PAY-005 | ✅ |
| BR-006 | REQ-PAY-006 | ✅ |
| BR-007 | REQ-PAY-007 | ✅ |
| BR-008 | REQ-DSCT-001 | ✅ |
| BR-009 | REQ-DSCT-002 | ✅ |
| BR-010 | REQ-DSCT-003 | ✅ |
| BR-011 | REQ-DSCT-004 | ✅ |
| BR-012 | REQ-DSCT-005 | ✅ |
| BR-013 | REQ-DSCT-006 | ✅ |
| BR-014 | REQ-DSCT-007 | ✅ |
| BR-015 | REQ-DSCT-008 | ✅ |
| BR-016 | REQ-CORR-001 | ✅ |
| BR-017 | REQ-CORR-002 | ✅ |
| BR-018 | REQ-CORR-003 | ✅ |
| BR-019 | REQ-ELEG-001 | ✅ |
| BR-020 | REQ-ELEG-002 | ✅ |
| BR-021 | REQ-ELEG-003 | ✅ |
| BR-022 | REQ-ELEG-004 | ✅ |
| BR-023 | REQ-ELEG-005 | ✅ |
| BR-024 | REQ-CAD-001 | ✅ |
| BR-025 | REQ-CAD-002 | ✅ |
| BR-026 | REQ-VAL-001 | ✅ |
| BR-027 | REQ-VAL-002 | ✅ |
| BR-028 | REQ-VAL-003 | ✅ |
| BR-029 | REQ-BATCH-001 | ✅ |
| BR-030 | REQ-BATCH-002 | ✅ |

> **Cobertura atual:** 30/30 BRs cobertas com REQ-ID e `source_legacy`.

---

## 4. Checklist da Etapa 1 (EARS)

- [x] Requisitos escritos em notação EARS
- [x] REQ-ID único por requisito
- [x] `source_legacy:` presente em 100% dos REQ-IDs deste documento
- [x] Critérios de aceitação testáveis em todos os REQ-IDs
- [x] Matriz BR → REQ atualizada

---

## 5. Sign-off de passagem (passo 6)

- [x] Etapa 1 (EARS) concluída com 30 REQ-IDs
- [x] Etapa 2 (ADRs) concluída com 4 ADRs
- [x] Etapa 3 (C4 L1/L2) concluída em arquivo dedicado
- [x] Etapa 4 (decisões de escopo) concluída
- [x] Etapa 5 (fluxo Spec-Kit) validada no workspace (`specify version` = 0.8.13)
- [x] Aprovação final do Product Owner registrada

Campos para assinatura da passagem #2:

- Product Owner (Par 1): Assinado  Data: 27/05/2026
- Enterprise Architect (Par 2): Assinado  Data: 27/05/2026
- Technical Lead (Par 3): Assinado  Data: 27/05/2026

