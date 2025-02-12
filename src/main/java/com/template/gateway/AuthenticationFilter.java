package com.template.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String CLASS = "[ AUTHENTICATION FILTER ]";

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${api.keys.auth}")
    private String authApiKey;

    @Value("${api.keys.core}")
    private String coreApiKey;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    private final String VALIDATION_TOKEN_FAILED = "[ AUTH API ]";

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
                    .uri("http://TEMPLATE-AUTH/template-auth/api/auth/validate/" + token.replace("Bearer ", ""))
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
                        return Mono.error(new JWTValidationException(String.format(VALIDATION_TOKEN_FAILED + " - %s", clientResponse.getMessage())));
                    });
        };
    }

    private ServerWebExchange buildExchange(ServerWebExchange exchange, String serviceToken) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Service-Token", serviceToken)
                .build();

        return exchange.mutate().request(request).build();
    }

    private String generateServiceToken(String path) {
        try {
            return ApiKey.getKey(path,
                    Map.of(
                            "template-auth", authApiKey,
                            "template-core", coreApiKey
                    )
            );
        } catch (IllegalAccessException e) {
            LOG.error("{} - Erro durante a geração do token da API - [{}]", CLASS, e.getMessage(), e);
            throw new APIKeyValidationException(e.getMessage());
        }
    }
}
