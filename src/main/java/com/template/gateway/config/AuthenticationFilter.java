package com.template.gateway.config;

import com.template.gateway.enums.*;
import com.template.gateway.exception.APIKeyValidationException;
import com.template.gateway.exception.JWTValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Filtro de autenticação para validação de tokens JWT em requisições do Gateway.
 * Esse filtro intercepta as requisições, verifica a presença de um token JWT no cabeçalho
 * e valida sua autenticidade por um serviço de autenticação externo.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String CLASS = "[ AUTHENTICATION FILTER ]";

    private final WebClient.Builder webClientBuilder;

    @Value("${api.keys.template-auth}")
    private String templateAuthApiKey;

    @Value("${api.keys.template-core}")
    private String templateCoreApiKey;

    /**
     * Classe de configuração para o filtro.
     */
    public static class Config {
    }

    private static final String VALIDATION_TOKEN_FAILED = "[ AUTH API ]";

    /**
     * Aplica o filtro de autenticação ao Gateway.
     *
     * @param config Configuração do filtro.
     * @return O {@link GatewayFilter} aplicado.
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (exchange.getRequest().getURI().getPath().contains("v3/api-docs") || exchange.getRequest().getURI().getPath().contains("api/auth/login")) {
                return chain.filter(exchange);
            }

            if (token == null) {
                LOG.error("{} - TOKEN INEXISTENTE NA REQUISIÇÃO", CLASS);
                throw new JWTValidationException("TOKEN INEXISTENTE NA REQUISIÇÃO");
            }

            return webClientBuilder.build()
                    .put()
                    .uri(MessageFormat.format("http://TEMPLATE-AUTH/template-auth/api/auth/validate/{0}", token.replace("Bearer ", "")))
                    .body(BodyInserters.fromValue(Map.of("path", exchange.getRequest().getURI().getPath())))
                    .exchangeToMono(response -> {
                        if (response.statusCode().isError()) {
                            throw new JWTValidationException(response.statusCode().toString());
                        }

                        String serviceToken = generateServiceToken(exchange.getRequest().getURI().getPath());
                        return chain.filter(buildExchange(exchange, serviceToken));
                    })
                    .onErrorResume(clientResponse -> {
                        LOG.error("{} - {} - [{}]", CLASS, VALIDATION_TOKEN_FAILED, clientResponse.getMessage());
                        return Mono.error(new JWTValidationException(MessageFormat.format("{0} - {1}", VALIDATION_TOKEN_FAILED, clientResponse.getMessage())));
                    });
        };
    }

    /**
     * Constrói um novo {@link ServerWebExchange} adicionando o token de serviço no cabeçalho da requisição.
     *
     * @param exchange     A requisição original.
     * @param serviceToken O token de serviço gerado.
     * @return Um novo {@code ServerWebExchange} com o cabeçalho atualizado.
     */
    private ServerWebExchange buildExchange(ServerWebExchange exchange, String serviceToken) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Service-Token", serviceToken)
                .build();

        return exchange.mutate().request(request).build();
    }

    /**
     * Gera um token de serviço com base no caminho da requisição.
     * O token é obtido a partir de um mapeamento de chaves API específicas para cada serviço.
     *
     * @param path O caminho da requisição que será usado para determinar a chave apropriada.
     * @return O token de serviço correspondente ao caminho fornecido.
     * @throws APIKeyValidationException Se ocorrer um erro ao gerar a chave da API.
     */
    private String generateServiceToken(String path) {
        try {
            return ApiKey.getKey(path,
                    Map.of(
                            "template-auth", templateAuthApiKey,
                            "template-core", templateCoreApiKey
                    )
            );
        } catch (IllegalAccessException e) {
            String errorMessage = String.format("%s - Erro durante a geração do token da API: %s", CLASS, e.getMessage());
            LOG.error(errorMessage, e);
            throw new APIKeyValidationException(errorMessage);
        }
    }
}
