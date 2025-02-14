# Guia de Configuração e Execução da Aplicação

Este guia contém instruções sobre como configurar as variáveis de ambiente necessárias e iniciar as aplicações relacionadas ao projeto.

## Configuração das Variáveis de Ambiente

Antes de iniciar as aplicações, certifique-se de configurar as seguintes variáveis de ambiente:

1. `ADMIN_PASSWORD`: Senha para o administrador.
2. `AUTH_API_KEY`: Chave secreta para o microservice de autenticação.
3. `CORE_API_KEY`: Chave secreta para o microservice central.
4. `DB_PASSWORD`: Senha para o banco de dados.
5. `DB_URL`: URL do banco de dados.
6. `DB_USERNAME`: Username para o banco de dados.
7. `EMAIL_SENDER`: E-mail responsável para enviar e-mails.
8. `EMAIL_SENDER_PASSWORD`: A senha do e-mail.
9. `EUREKA_URL`: URL onde está o registry das aplicações.
10. `JWT_SECRET`: Com um base64 válido.
11. `LOG_PATH`: Caminho para a geração dos logs.

Certifique-se de definir essas variáveis de ambiente conforme os requisitos do sistema e da aplicação.

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
- [URL do Swagger](http://localhost:8081/webjars/swagger-ui/index.html): Documentação interativa da aplicação template.