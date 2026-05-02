# Meu SIM — API (desatualizado)

API do **Sistema Integrado de Gestão para Reforço Acadêmico**, desenvolvida como base SaaS multi-escola.  
Oferece autenticação segura com JWT/RSA, controle de acesso por perfis, integração com Mercado Pago e suporte a múltiplos ambientes de execução.

---

## Tecnologias

| Categoria | Tecnologia                                         |
|---|----------------------------------------------------|
| Linguagem / Runtime | Java 21                                            |
| Framework | Spring Boot 4                                      |
| Segurança | Spring Security · OAuth2 Resource Server (JWT/RSA) |
| Persistência | Spring Data JPA · PostgreSQL · H2 (testes)         |
| Cache | Redis · Bucket4j (rate limiting)                   |
| Mensageria / Email | Spring Mail · Thymeleaf                            |
| Pagamentos | Mercado Pago SDK                                   |
| Observabilidade | Spring Actuator                                    |
| Build / Cobertura | Maven · JaCoCo                                     |

...

---

## Perfis de ambiente

O projeto utiliza três profiles Spring:

### `dev` - Desenvolvimento local
- Banco: PostgreSQL local (via Docker)
- Logs detalhados habilitados
- Integrações em modo sandbox
- Usuários criados já como ativos (sem fluxo de ativação por e-mail)

### `test` —- Testes automatizados
- Banco: H2 em memória
- Isolado de integrações externas
- Usado pelo JaCoCo para cobertura

### `prod` - Produção
- Banco: PostgreSQL
- Integrações reais (Mercado Pago)
- Segurança reforçada (CORS restrito, logs reduzidos)

---

## Configuração de ambiente

Crie o arquivo `env.properties` na raiz do projeto com as variáveis abaixo para rodar na máquina local.
> ⚠️ **Nunca versione este arquivo.** Adicione ao `.gitignore`.
```properties
# Aplicação
SPRING_PROFILE=dev
SPRING_SERVER_PORT=8080

# CORS
FRONT_END_URL=http://localhost:4200

# Banco de dados — Produção
DATABASE_URL_PROD=
DATABASE_USERNAME_PROD=
DATABASE_PASSWORD_PROD=

# Redis
REDIS_HOST=

# Email - Teste
EMAIL_HOST_TEST=
EMAIL_PORT_TEST=

# Banco de dados — Testes
DATABASE_URL_TEST=
DATABASE_USERNAME_TEST=sa
DATABASE_PASSWORD_TEST=

# Mercado Pago — Produção
MERCADO_PAGO_ACCESS_TOKEN_PROD=
MERCADO_PAGO_NOTIFICATION_PROD=
MERCADO_PAGO_WEBHOOK_SECRET_PROD=

# Mercado Pago — Sandbox
MERCADO_PAGO_ACCESS_TOKEN_TEST=
MERCADO_PAGO_NOTIFICATION_TEST=
MERCADO_PAGO_WEBHOOK_SECRET_TEST=
```

| Variável | Descrição |
|---|---|
| `SPRING_PROFILE` | Profile ativo: `dev`, `test` ou `prod` |
| `SPRING_SERVER_PORT` | Porta da aplicação |
| `FRONT_END_URL` | URL do frontend (usado para CORS) |
| `DATABASE_*` | Credenciais de banco por ambiente |
| `MERCADO_PAGO_*` | Credenciais e endpoints de pagamento |

---

## Segurança — JWT com RSA

A autenticação utiliza tokens JWT assinados com chaves RSA (par assimétrico).  
As chaves **não devem ser versionadas** e precisam estar em:
```
src/main/resources/keys/
├── app.key   ← chave privada
└── app.pub   ← chave pública
```

---

### Gerando as chaves
```bash
# 1. Chave privada
openssl genrsa -out app.key 2048

# 2. Chave pública derivada
openssl rsa -in app.key -pubout -out app.pub

# 3. Mover para o projeto
mv app.key app.pub src/main/resources/keys/
```

> ⚠️ Adicione `src/main/resources/keys/` ao `.gitignore`.

---

## Docker

O ambiente de desenvolvimento é gerenciado via `docker-compose`, mantido em diretório separado do projeto principal.  
Sobe os serviços de infraestrutura necessários (PostgreSQL, Redis) sem precisar instalá-los localmente.

---

## Funcionalidades técnicas

### Autenticação & Autorização
- JWT assinado com RSA (chave assimétrica)
- Controle de acesso por perfis: `SYSTEM_ADMIN`, `SCHOOL_ADMIN`, `COLLABORATOR`, `LEGAL_GUARDIAN`

### Cache & Rate Limiting
- Redis para cache em memória
- Bucket4j para controle de taxa de requisições por IP/usuário

### Pagamentos
- Integração com Mercado Pago
- Suporte a PIX com geração de QR Code
- Webhook para confirmação automática de pagamentos

### Email
- Envio via SMTP com templates HTML (Thymeleaf)
- Usado em ativação de conta, notificações e faturas

### Observabilidade
- Spring Actuator: endpoints de health, metrics e info

---

## Testes

- Framework: Spring Boot Test
- Banco: H2 em memória (profile `test`)
- Cobertura: JaCoCo

```bash
# Rodar testes com relatório de cobertura
./mvnw test jacoco:report
```

---

## Observações

- Nunca versionar o `env.properties`
- Nunca versionar as chaves RSA
- Utilizar valores de sandbox no ambiente de desenvolvimento
- Garantir que Redis esteja disponível quando habilitado em todos os profiles
