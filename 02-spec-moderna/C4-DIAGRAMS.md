<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# C4 Diagrams — SIFAP 2.0

## C4-L1 Contexto

```mermaid
C4Context
 title Diagrama de Contexto - SIFAP 2.0

 Person(operator, "Operador", "Registra e processa pagamentos")
 Person(auditor, "Auditor", "Consulta trilhas e relatorios")
 Person(admin, "Administrador", "Gerencia usuarios e configuracoes")

 System(sifap, "SIFAP 2.0", "Sistema de cadastro, elegibilidade e processamento de pagamentos")

 System_Ext(idp, "Gov.br / IdP OIDC", "Provedor de autenticacao")
 System_Ext(tesouro, "Sistema Financeiro Externo", "Recebe arquivos/ordens para liquidacao")

 Rel(operator, sifap, "Opera ciclo de pagamento", "HTTPS")
 Rel(auditor, sifap, "Audita operacoes e eventos", "HTTPS")
 Rel(admin, sifap, "Administra acessos e parametros", "HTTPS")
 Rel(sifap, idp, "Autentica usuarios", "OAuth2/OIDC")
 Rel(sifap, tesouro, "Envia dados financeiros", "API/Arquivo")
```

## C4-L2 Containers

```mermaid
C4Container
 title Diagrama de Containers - SIFAP 2.0 (Modular Monolith)

 Person(user, "Usuario", "Operador, Auditor ou Admin")
 System_Ext(idp, "Gov.br / IdP OIDC", "Autenticacao")
 System_Ext(tesouro, "Sistema Financeiro Externo", "Liquidacao financeira")

 Container_Boundary(sifap, "SIFAP 2.0") {
   Container(frontend, "Web App", "Next.js 15", "UI para cadastro, consulta, operacao e auditoria")
   Container(api, "API", "Java 21 + Spring Boot 3", "REST /api/v1 e orquestracao de modulos")
   Container(batch, "Batch", "Spring Batch", "Processamento mensal idempotente por competencia")
   ContainerDb(db, "PostgreSQL 16", "Banco relacional", "Beneficiario, Programa, Pagamento, Auditoria")
 }

 Rel(user, frontend, "Usa", "HTTPS")
 Rel(frontend, api, "Consome", "REST/JSON")
 Rel(api, idp, "Valida token", "OAuth2/OIDC")
 Rel(api, db, "Le/escreve", "JDBC")
 Rel(batch, db, "Le/escreve", "JDBC")
 Rel(batch, tesouro, "Publica lote financeiro", "API/Arquivo")
```

## Observacoes

- L1 e L2 focam no escopo de modernizacao do workshop.
- Nivel de componentes (L3) fica opcional e pode ser detalhado no Estagio 3 se necessario.
