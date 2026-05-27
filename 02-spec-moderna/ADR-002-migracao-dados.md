<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-002: Estrategia de migracao de dados incremental

**Data**: 27/05/2026  
**Status**: Aceita  
**Decisores**: Par 2 (EA + SA), com suporte do Par 4 (DBA + QA)

## Contexto

Os dados legados de beneficiario, programa, pagamento e auditoria sao criticos e nao podem sofrer perda durante transicao. Migracao em big bang eleva risco de indisponibilidade e inconsistencias, especialmente no ciclo mensal batch.

## Opcoes Consideradas

### Opcao 1: Big Bang

- **Descricao**: migrar todo historico e virar chave em uma unica janela.
- **Vantagens**: janela unica de transicao.
- **Desvantagens**: alto risco operacional e rollback complexo.

### Opcao 2: Incremental por dominio e competencia

- **Descricao**: migrar em lotes controlados por dominio (cadastro, pagamento, auditoria) e por competencia, com reconciliacao automatizada.
- **Vantagens**: menor risco, validacao por etapas, rollback localizado.
- **Desvantagens**: periodo de coexistencia exige controles de consistencia.

### Opcao 3: Reescrita sem migracao historica

- **Descricao**: iniciar base limpa e importar apenas dados atuais.
- **Vantagens**: simplifica schema inicial.
- **Desvantagens**: inviavel para auditoria e compliance.

## Decisao

**Decidimos adotar migracao incremental por dominio e por competencia, com reconciliacao obrigatoria apos cada carga.**

## Justificativa

A estrategia incremental reduz impacto no negocio, permite verificacao de equivalencia e minimiza risco de perda de trilha historica.

## Consequencias

### Positivas

- Menor risco de parada total.
- Validacao continua de qualidade de dados.
- Suporte a rollback por lote.

### Negativas

- Maior esforco de operacao no periodo de coexistencia.
- Necessidade de monitoramento de reconciliacao.

### Riscos

- **Risco**: divergencia entre legado e novo durante coexistencia.  
  **Mitigacao**: reconciliacao diaria por contagem, soma financeira e chaves de negocio.

## Referencias

- 01-arqueologia/discovery-report.md
- 01-arqueologia/business-rules-catalog.md
- REQ-DSCT-008, REQ-CORR-003, REQ-BATCH-002
