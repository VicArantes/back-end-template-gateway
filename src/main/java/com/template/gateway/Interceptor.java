package com.template.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Esta Class intercepta as request para validações.
 */
@Component
public class Interceptor implements GlobalFilter {

    /**
     * Lista das API_KEYS disponíveis
     */
    private static final List<String> API_KEYS = Arrays.asList("template-core", "template-auth");

    /**
     * Verifica se a request precisa de validação.
     *
     * @param path O path da request.
     * @return Se a request precisa de validação.
     */
    private boolean needValidation(String path) {
        return !path.contains("/swagger-ui/") && !path.contains("/api-docs") && !path.contains("/login");
    }

    /**
     * Verifica se a apiKey é válida.
     *
     * @param apiKey A API_KEY fornecida no header.
     * @return Se a apiKey é válida.
     */
    private boolean validateApiKey(String apiKey) {
        boolean isValidApiKey = false;

        for (String ak : API_KEYS) {
            if (apiKey.equals(ak)) {
                isValidApiKey = true;
                break;
            }
        }

        return isValidApiKey;
    }

    /**
     * Realiza a validação da chave de API em uma solicitação e processa a cadeia de filtros
     * se a validação for bem-sucedida.
     *
     * @param exchange O objeto ServerWebExchange que representa a troca entre o cliente e o servidor.
     * @param chain    O GatewayFilterChain que permite a execução subsequente de filtros.
     * @return Um Mono<Void> representando a conclusão do processamento do filtro.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (this.needValidation(request.getPath().value())) {
            String apiKey = request.getHeaders().getFirst("API-KEY");

            if (apiKey == null || apiKey.isEmpty() || !this.validateApiKey(apiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"message\": \"API key inválida\"}".getBytes())));
            }
        }

        return chain.filter(exchange);
    }

}