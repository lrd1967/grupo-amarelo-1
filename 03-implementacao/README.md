# Implementacao SIFAP Moderno (Java 21 + Angular)

Esta pasta contem uma implementacao funcional baseada na Spec Moderna (02-spec-moderna) e nas regras extraidas do legado em 01-arqueologia.

## Estrutura

- backend: Spring Boot 3.3 com Java 21, JPA, Flyway e endpoints REST
- frontend: Angular standalone consumindo a API REST

## Requisitos cobertos no MVP

- REQ-CAD-001, REQ-CAD-002
- REQ-VAL-001, REQ-VAL-002, REQ-VAL-003
- REQ-ELEG-001..005 (avaliacao de elegibilidade)
- REQ-PAY-001..007 (calculo base)
- REQ-DSCT-002, REQ-DSCT-004, REQ-DSCT-006 (descontos e teto)
- REQ-BATCH-001, REQ-BATCH-002 (lote simplificado)

## Backend - executar

1. Entrar na pasta backend:
	cd 03-implementacao/backend
2. Rodar app:
	mvn spring-boot:run

A API sobe em http://localhost:8080

Endpoints principais:
- POST /api/v1/beneficiaries
- GET /api/v1/beneficiaries
- POST /api/v1/payments/calculate
- POST /api/v1/payments/batch/{competenceMonth}
- GET /api/v1/payments

## Frontend Angular - executar

1. Entrar na pasta frontend:
	cd 03-implementacao/frontend
2. Instalar dependencias:
	npm install
3. Subir app:
	npm start

A interface sobe em http://localhost:4200

## Observacoes

- A migracao inicial de banco esta em backend/src/main/resources/db/migration/V1__init_sifap_modern.sql.
- A implementacao foi organizada em package-by-feature (beneficiary, payment, shared), seguindo modular monolith.
