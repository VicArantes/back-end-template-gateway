# Guia de Configuração e Execução da Aplicação

Este guia contém instruções sobre como configurar as variáveis de ambiente necessárias e iniciar as aplicações relacionadas ao projeto.

## Configuração das Variáveis de Ambiente

Antes de iniciar as aplicações, certifique-se de configurar as seguintes variáveis de ambiente:

1. `ADMIN_PASSWORD`: Senha para o administrador.
2. `DB_PASSWORD`: Senha para o banco de dados.

Certifique-se de definir essas variáveis de ambiente de acordo com os requisitos do sistema e da aplicação.

## Subindo as Aplicações

Para iniciar as aplicações relacionadas ao projeto, siga estas etapas:

### 1. back-end-template-server
### 2. back-end-template-gateway
### 3. back-end-template-auth
### 4. back-end-template-core

Certifique-se de iniciar essas aplicações na ordem correta para garantir que os serviços dependentes estejam disponíveis.

## URLs Importantes

Aqui estão algumas URLs importantes para acesso aos serviços:

- [URL do Eureka](http://localhost:8080/): Serviço de descoberta de microservices (Eureka).
- [URL do Swagger para o template Core](http://localhost:8081/template-core/swagger-ui/index.html#/): Documentação interativa para o serviço template Core.
- [URL do Swagger para o template Auth](http://localhost:8081/template-auth/swagger-ui/index.html#/): Documentação interativa para o serviço template Auth.
