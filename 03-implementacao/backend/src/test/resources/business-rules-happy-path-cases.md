# Casos de Teste de Caminho Feliz por Regra (BR-001 a BR-030)

## Objetivo
Registrar um caso de teste de caminho feliz para cada regra mapeada em 01-arqueologia/business-rules-catalog.md e indicar se a regra ja esta automatizada no backend atual.

## Matriz de casos

| Regra | Caso de caminho feliz | Status no backend atual | Evidencia automatizada |
|---|---|---|---|
| BR-001 | Calculo com competencia valida (ex.: 1) deve processar normalmente. | Implementada | PaymentServiceTest.br001HappyPathShouldAcceptCompetenceWithinRange |
| BR-002 | Beneficiario com status A pode calcular pagamento. | Implementada | PaymentServiceTest.br001HappyPathShouldAcceptCompetenceWithinRange |
| BR-003 | Codigo de regiao entre 1 e 25 aplica fator regional > 1. | Implementada | PaymentServiceTest.br003HappyPathShouldApplyRegionalFactorForRegionWithinRange |
| BR-004 | Dependentes acima de 4 aplicam fator familiar da ultima faixa. | Implementada | PaymentServiceTest.br004HappyPathShouldApplyFamilyFactorForMoreThanFourDependents |
| BR-005 | Idade 65+ aplica fator etario de maior faixa. | Implementada | PaymentServiceTest.br005HappyPathShouldApplyAgeFactorForAge65OrMore |
| BR-006 | Competencia 12 gera tipo de pagamento D com processamento bem-sucedido. | Implementada | PaymentServiceTest.calculateAndSaveShouldApplyDecemberBonusAndDeductionCap |
| BR-007 | Programa tipo A em dezembro recebe abono de 15%. | Implementada | PaymentServiceTest.calculateAndSaveShouldApplyDecemberBonusAndDeductionCap |
| BR-008 | Valor bruto > 500 aplica desconto social simplificado de 3%. | Nao implementada | Pendente de implementacao no PaymentService |
| BR-009 | Valor liquido final deve permanecer nao negativo (minimo zero). | Implementada | PaymentServiceTest.calculateAndSaveShouldNotReturnNegativeNetAmount |
| BR-010 | Faixas fixas de contribuicao social (3/5/7/9%) calculadas corretamente. | Nao implementada | Pendente de implementacao no PaymentService |
| BR-011 | Desconto nao judicial respeita teto de 30% com truncamento para 2 casas. | Implementada | PaymentServiceTest.calculateAndSaveShouldApplyDecemberBonusAndDeductionCap |
| BR-012 | Desconto dentro da vigencia temporal e aplicado normalmente. | Nao implementada | Pendente de modelagem de descontos com datas |
| BR-013 | Desconto judicial percentual/fixo e aplicado sem teto de 30%. | Implementada (parcial) | PaymentServiceTest.calculateAndSaveShouldApplyDecemberBonusAndDeductionCap |
| BR-014 | Desconto sindical aplica 1% fixo do bruto. | Nao implementada | Pendente de tipos de desconto no dominio |
| BR-015 | Apos calculo, valor de desconto e persistido no pagamento. | Implementada | PaymentServiceTest.br015HappyPathShouldPersistCalculatedPayment |
| BR-016 | Faixa temporal valida (competencia inicial <= final) permite seguir com correcao. | Nao implementada | Pendente de caso de uso de correcao retroativa |
| BR-017 | Pagamento ainda nao corrigido pode entrar no fluxo de correcao. | Nao implementada | Pendente de caso de uso de correcao retroativa |
| BR-018 | Correcao positiva e gravada com marcacao de corrigido. | Implementada (parcial) | PaymentServiceTest.calculateAndSaveShouldApplyDecemberBonusAndDeductionCap |
| BR-019 | Beneficiario da regiao 99 e elegivel automaticamente. | Implementada | BeneficiaryServiceTest.evaluateEligibilityShouldReturnTrueForSpecialRegion |
| BR-020 | Programa A com renda/dep/documentos validos e elegivel. | Implementada | BeneficiaryServiceTest.br020HappyPathProgramAShouldBeEligibleWhenConstraintsAreSatisfied |
| BR-021 | Programa P com idade minima de 60 e elegivel. | Implementada | BeneficiaryServiceTest.br021HappyPathProgramPShouldBeEligibleWithAge60OrMore |
| BR-022 | Programa T com idade entre 16 e 65 e elegivel. | Implementada | BeneficiaryServiceTest.br022HappyPathProgramTShouldBeEligibleWithinAgeRange |
| BR-023 | Quando flags especiais exigem NIS/dependentes, cadastro valido segue elegivel. | Implementada (aproximada) | BeneficiaryServiceTest.br023HappyPathProgramAShouldBeEligibleWhenSpecialFlagsAreSatisfied |
| BR-024 | Operacao de cadastro A e aceita no upsert. | Implementada | BeneficiaryServiceTest.br024HappyPathShouldAcceptOperationA |
| BR-025 | Inclusao com idade <= 75 inicia com status A. | Implementada | BeneficiaryServiceTest.br025HappyPathShouldSetInitialStatusAWhenAgeIsAtMost75 |
| BR-026 | CPF com prefixo 000 passa na excecao de validacao. | Implementada | BeneficiaryServiceTest.br026HappyPathShouldAcceptCpfWith000PrefixAsException |
| BR-027 | Status cadastral valido (A/S/C/I/D) e aceito no fluxo de cadastro. | Implementada | BeneficiaryServiceTest.br025HappyPathShouldSetInitialStatusAWhenAgeIsAtMost75 |
| BR-028 | Prefixo especial de CPF (999) faz bypass e retorna valido. | Implementada | BeneficiaryServiceTest.br028HappyPathShouldBypassValidationForCpfPrefix999 |
| BR-029 | Batch ignora CPF duplicado para evitar pagamento em duplicidade. | Implementada | PaymentServiceTest.runBatchShouldGeneratePaymentsOnlyForUniqueActiveBeneficiariesWithoutPayment |
| BR-030 | Batch gera pagamento apenas para beneficiario ativo e nao pago na competencia. | Implementada (parcial) | PaymentServiceTest.br030HappyPathShouldGenerateForActiveNotPaidBeneficiary |
