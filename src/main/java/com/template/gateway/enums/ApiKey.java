package com.template.gateway.enums;

import java.util.Base64;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Enumeração responsável pelo gerenciamento de chaves de API.
 * Cada instância da enum codifica uma chave de entrada em Base64.
 */
public enum ApiKey {
    TEMPLATE_AUTH(tempKey -> Base64.getEncoder().encodeToString(tempKey.getBytes())),
    TEMPLATE_CORE(tempKey -> Base64.getEncoder().encodeToString(tempKey.getBytes()));

    /**
     * Operador responsável pela transformação da chave.
     */
    private final UnaryOperator<String> key;

    /**
     * Construtor da enum ApiKey.
     *
     * @param key Função que recebe uma string e retorna a chave transformada.
     */
    ApiKey(UnaryOperator<String> key) {
        this.key = key;
    }

    private static final String TEMPLATE_AUTH_KEY = "template-auth";
    private static final String TEMPLATE_CORE_KEY = "template-core";

    /**
     * Aplica a transformação da chave.
     *
     * @param api Chave original.
     * @return Chave transformada em Base64.
     */
    private String applyKey(String api) {
        return key.apply(api);
    }

    /**
     * Retorna a chave correspondente ao caminho fornecido.
     *
     * @param path Caminho da requisição.
     * @param keys Mapa contendo as chaves disponíveis.
     * @return Chave transformada em Base64.
     * @throws IllegalAccessException Se o caminho não corresponder a uma chave válida.
     */
    public static String getKey(String path, Map<String, String> keys) throws IllegalAccessException {
        if (path.contains(TEMPLATE_AUTH_KEY)) {
            return TEMPLATE_AUTH.applyKey(keys.get(TEMPLATE_AUTH_KEY));
        }

        if (path.contains(TEMPLATE_CORE_KEY)) {
            return TEMPLATE_CORE.applyKey(keys.get(TEMPLATE_CORE_KEY));
        }

        throw new IllegalAccessException(String.format("[API KEY] - Nenhuma chave mapeada para o caminho da requisição: [%s]", path));
    }

}