# Backend BrasaOS

Backend Java 21 + Spring Boot 4.1.2 que atende o frontend administrativo e o frontend mobile.

## Estrutura
- `controller`: endpoints REST
- `service`: regras de negócio e isolamento multi-tenant
- `repository`: Spring Data JPA
- `model`: entidades JPA
- `dto`: contratos JSON dos frontends
- `mapper`: conversão entidade/DTO
- `security`: JWT e principal com `restaurantId`
- `config`: Security, CORS, repositórios e bootstrap
- `exception`: erros padronizados

## Rodar
```bash
mvn spring-boot:run
```
API: `http://localhost:8080/api`

O perfil padrão persiste em H2 arquivo: `./data/restaurant.mv.db`.

## Usuários iniciais
- ADMIN: `admin@restaurante.com` / `Admin@123`
- COZINHA: `cozinha@restaurante.com` / `Cozinha@123`
- slug: `meu-restaurante`

O bootstrap cria somente restaurante, usuários e mesas. Não cria produtos, categorias, pedidos ou faturamento fictício.

## PostgreSQL
```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
Antes de produção, crie migrations em `src/main/resources/db/migration` e mantenha `ddl-auto: validate`.

## Segurança
- JWT stateless
- senha BCrypt
- endpoints `/api/public/**` públicos
- painel ADMIN limitado a `ROLE_ADMIN`
- KDS/pedidos disponível para usuários autenticados
- todo acesso administrativo filtra pelo `restaurantId` contido no JWT

## Fechamento da conta
1. Mobile envia `FECHAR_CONTA`.
2. Mesa muda para `FECHAMENTO_SOLICITADO`.
3. Admin confirma em `POST /api/mesas/{numero}/fechar`.
4. Pedidos ficam `FECHADO`, chamados são fechados e mesa fica `FECHADA`.
5. Mobile recebe `FECHADA` e apaga os dados locais daquela mesa.
