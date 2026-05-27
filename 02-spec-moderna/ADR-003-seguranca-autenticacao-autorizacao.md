<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-003: Autenticacao e autorizacao com OAuth2/OIDC e RBAC

**Data**: 27/05/2026  
**Status**: Aceita  
**Decisores**: Par 2 (EA + SA), com revisao do Par 5 (DevOps + TW)

## Contexto

O SIFAP manipula dados sensiveis e operacoes financeiras. E necessario autenticar usuarios de forma centralizada e aplicar autorizacao por papeis operacionais (OPERATOR, AUDITOR, ADMIN), mantendo trilha auditavel.

## Opcoes Consideradas

### Opcao 1: OAuth2/OIDC + JWT + RBAC

- **Descricao**: autenticacao federada via provedor OIDC e autorizacao por roles no backend.
- **Vantagens**: padrao moderno, integracao com ecossistema gov, boa auditabilidade.
- **Desvantagens**: exige configuracao inicial de seguranca e claims.

### Opcao 2: Sessao local com usuario/senha no proprio sistema

- **Descricao**: autenticar localmente no SIFAP 2.0.
- **Vantagens**: implementacao direta.
- **Desvantagens**: aumenta superficie de ataque e responsabilidade sobre credenciais.

### Opcao 3: Autorizacao apenas no frontend

- **Descricao**: bloquear opcoes na UI sem enforce no backend.
- **Vantagens**: baixo custo inicial.
- **Desvantagens**: inseguro e inadequado para sistema critico.

## Decisao

**Decidimos usar OAuth2/OIDC para autenticacao e RBAC no backend para autorizacao de operacoes.**

## Justificativa

A decisao atende requisitos de seguranca e compliance sem depender de controles apenas de interface. Garante enforcement no backend e trilha de quem executou cada acao critica.

## Consequencias

### Positivas

- Seguranca consistente entre UI e API.
- Melhor alinhamento com exigencias de compliance.
- Menor risco de privilegio indevido em operacoes criticas.

### Negativas

- Necessidade de configurar e manter integracao com IdP.
- Dependencia de disponibilidade do provedor de identidade.

### Riscos

- **Risco**: mapeamento incorreto de roles para casos criticos.  
  **Mitigacao**: testes de autorizacao por REQ e revisao de matriz de acesso em PR.

## Referencias

- 02-spec-moderna/SPECIFICATION.md
- REQ-VAL-002, REQ-BATCH-002
- MYS-005 em 01-arqueologia/discovery-report.md
