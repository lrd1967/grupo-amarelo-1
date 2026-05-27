<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-001: Arquitetura alvo em Modular Monolith

**Data**: 27/05/2026  
**Status**: Aceita  
**Decisores**: Par 2 (EA + SA), com validação do Par 1 (PO)

## Contexto

O SIFAP legado concentra regras de alto acoplamento entre cadastro, elegibilidade, calculo, descontos, correcao e batch. A migracao direta para microsservicos no Estagio 3 aumentaria custo de coordenacao e risco de regressao funcional em regras criticas. O time precisa manter coesao de dominio com fronteiras claras e entrega incremental.

## Opcoes Consideradas

### Opcao 1: Modular Monolith

- **Descricao**: aplicacao unica com modulos por dominio (beneficiary, eligibility, payment, discount, correction, batch, audit), contratos internos explicitos e banco compartilhado controlado.
- **Vantagens**: menor complexidade operacional inicial, transacoes locais simples, melhor velocidade de entrega no workshop.
- **Desvantagens**: risco de acoplamento interno se disciplina modular nao for seguida.

### Opcao 2: Microsservicos imediatos

- **Descricao**: servicos independentes por dominio com comunicacao via APIs/eventos.
- **Vantagens**: isolamento forte e escalabilidade independente por dominio.
- **Desvantagens**: complexidade de observabilidade, transacoes distribuidas e deploy, custo alto para o prazo.

### Opcao 3: Big Ball of Mud modernizado

- **Descricao**: portar o legado para Spring Boot sem separacao por modulo.
- **Vantagens**: menor esforco inicial de desenho.
- **Desvantagens**: perpetua divida tecnica e dificulta evolucao.

## Decisao

**Decidimos adotar arquitetura Modular Monolith como alvo da modernizacao inicial do SIFAP 2.0.**

## Justificativa

A escolha preserva velocidade de entrega e reduz risco de regressao no nucleo financeiro, ao mesmo tempo em que cria fronteiras de dominio para evolucao futura. Permite refatorar para microsservicos posteriormente, se houver evidencia de ganho real.

## Consequencias

### Positivas

- Entrega mais rapida e com menor risco no Estagio 3.
- Melhor rastreabilidade BR -> REQ -> modulo.
- Curva operacional menor para equipe.

### Negativas

- Escalabilidade horizontal menos granular inicialmente.
- Necessidade de governanca interna para evitar acoplamento entre modulos.

### Riscos

- **Risco**: virar monolito sem modularidade real.  
  **Mitigacao**: enforce package-by-feature, testes por modulo e revisao arquitetural em PR.

## Referencias

- 02-spec-moderna/SPECIFICATION.md
- 01-arqueologia/business-rules-catalog.md
- REQ-PAY-001, REQ-PAY-006, REQ-BATCH-002
