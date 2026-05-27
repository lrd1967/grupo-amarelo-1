<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-004: Estrategia de processamento batch mensal

**Data**: 27/05/2026  
**Status**: Aceita  
**Decisores**: Par 2 (EA + SA), com alinhamento do Par 3 (TL + Dev)

## Contexto

O processamento batch mensal e o nucleo de geracao de pagamentos. As regras de elegibilidade, deduplicacao por CPF, controle de competencia e bloqueios por status devem ser preservadas com rastreabilidade.

## Opcoes Consideradas

### Opcao 1: Reprocessar tudo sem idempotencia

- **Descricao**: executar lote completo sem marcacao de progresso nem bloqueios por estado.
- **Vantagens**: implementacao inicial simples.
- **Desvantagens**: alto risco de duplicidade e inconsistencias.

### Opcao 2: Batch idempotente por competencia com checkpoints

- **Descricao**: executar lote com chave de competencia, deduplicacao, regras de bloqueio e checkpoints para retomada.
- **Vantagens**: reduz duplicidade, facilita recuperacao e auditoria.
- **Desvantagens**: exige desenho de estado de execucao.

### Opcao 3: Processamento apenas online

- **Descricao**: eliminar batch e processar somente sob demanda da UI.
- **Vantagens**: arquitetura simplificada em runtime.
- **Desvantagens**: nao atende volume e janela operacional do dominio.

## Decisao

**Decidimos implementar processamento batch idempotente por competencia, com deduplicacao de CPF e checkpoints de execucao.**

## Justificativa

A opcao preserva as regras mais criticas do legado e reduz o risco de pagamento duplicado, suportando retomada segura em caso de falha.

## Consequencias

### Positivas

- Controle de duplicidade e reprocessamento seguro.
- Melhor auditabilidade operacional.
- Maior previsibilidade no fechamento mensal.

### Negativas

- Maior complexidade de implementacao de estado do job.
- Necessidade de monitoramento operacional dedicado.

### Riscos

- **Risco**: checkpoint inconsistente pode bloquear reprocessamento valido.  
  **Mitigacao**: testes de falha/retomada e operacao manual assistida para desbloqueio controlado.

## Referencias

- 02-spec-moderna/SPECIFICATION.md
- REQ-BATCH-001, REQ-BATCH-002, REQ-CORR-002
- BR-029, BR-030
