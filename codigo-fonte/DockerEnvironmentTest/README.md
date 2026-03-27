# Docker Environment - Test

Este diretório contém a configuração completa do ambiente de desenvolvimento integrado utilizando Docker.

O objetivo é permitir que toda a infraestrutura necessária para o sistema rode localmente de forma isolada, incluindo serviços externos simulados.

---

## Objetivo

Este ambiente permite:

- Executar a API Spring em conjunto com seus serviços dependentes
- Testar integrações externas (email, pagamentos, storage)
- Simular ambiente próximo ao de produção
- Expor a aplicação para webhooks externos via ngrok

---

## Serviços disponíveis

O `docker-compose.yml` sobe os seguintes serviços:

### API
- `spring_api`
- Aplicação principal (Spring Boot)

### Banco de dados
- `postgres`
- PostgreSQL (ambiente de teste)

### Cache
- `redis`
- Utilizado para cache e controle de rate limiting

### Email
- `mailhog`
- Servidor SMTP fake para testes
- Interface web: http://localhost:8025

### Armazenamento (S3)
- `minio`
- Simula um serviço S3
- API: http://localhost:9000  
- Painel: http://localhost:9001

### Exposição externa
- `ngrok`
- Permite acesso externo à API e Interface Web do projeto (webhooks, testes externos)
- Painel: http://localhost:4040

---

## Arquivo `.env`

O arquivo `.env` é responsável por centralizar as variáveis de ambiente utilizadas pelos containers.

Ele define principalmente:

- Configuração da API
- Conexão com banco
- Integrações externas
- Comunicação entre containers

### Deve conter:

NGROK_AUTHTOKEN=
SPRING_PROFILE=
SPRING_SERVER_PORT=
FRONT_END_URL=
DATABASE_URL_PROD=
DATABASE_USERNAME_PROD=
DATABASE_PASSWORD_PROD=
DATABASE_URL_TEST=jdbc:postgresql://postgres:5432/db
DATABASE_USERNAME_TEST=
DATABASE_PASSWORD_TEST=
REDIS_HOST=redis
EMAIL_HOST_TEST=mailhog
EMAIL_PORT_TEST=1025
MERCADO_PAGO_ACCESS_TOKEN_PROD=
MERCADO_PAGO_NOTIFICATION_PROD=
MERCADO_PAGO_WEBHOOK_SECRET_PROD=
MERCADO_PAGO_ACCESS_TOKEN_TEST=
MERCADO_PAGO_NOTIFICATION_TEST=https://ngrok/api/v1/webhooks/mercado-pago
MERCADO_PAGO_WEBHOOK_SECRET_TEST=

### Observações

- Os nomes dos hosts (ex: `postgres`, `redis`, `mailhog`) correspondem aos nomes dos serviços no Docker
- Não utilizar `localhost` para comunicação entre containers
- Nunca versionar credenciais reais

---

## ngrok

O serviço `ngrok` é utilizado para expor a API local para a internet, permitindo:

- Recebimento de webhooks (ex: Mercado Pago)
- Testes externos
- Acesso à interface web da aplicação

### Configuração

Arquivo: ``ngrok.yml``

Exemplo:

``
version: "2"

tunnels:
  frontend:
    proto: http
    addr: host.docker.internal:4200

  backend:
    proto: http
    addr: spring_api:8080
``

Painel: ``http://localhost:4040``

---

## Comunicação entre serviços

Todos os serviços estão conectados à mesma rede Docker: ``app_net``

Isso permite comunicação interna utilizando o nome do container como host:

| Serviço   | Host utilizado |
|----------|---------------|
| Postgres | postgres      |
| Redis    | redis         |
| Mailhog  | mailhog       |
| Minio    | minio         |
| API      | spring_api    |

---

## Como executar

Na raiz deste diretório:

``docker-compose up --build``

Para rodar em background:

``docker-compose up -d``

Para parar:

``docker-compose down``

---

## Dependências entre serviços

A API depende de:

- Postgres (healthcheck ativo)
- Redis
- Mailhog
- Minio

O ngrok depende da API estar disponível.

---

## Boas práticas

- Sempre validar se o `.env` está correto antes de subir os containers
- Garantir que a porta 8080, 5433, 6379, 1025, 8025, 9000, 9001 e 4040 estejam livres
- Utilizar tokens de sandbox para integrações externas
- Não expor dados sensíveis no repositório

---

## Observações finais

Este ambiente foi projetado para desenvolvimento e testes locais.

Não deve ser utilizado como ambiente de produção.