package com.template.gateway;

import java.util.Base64;
import java.util.Map;
import java.util.function.Function;

public enum ApiKey {
    TEMPLATE_AUTH((key) -> {
        byte[] encodedKey = Base64.getEncoder().encode(key.getBytes());
        return new String(encodedKey);
    }),
    TEMPLATE_CORE((key) -> {
        byte[] encodedKey = Base64.getEncoder().encode(key.getBytes());
        return new String(encodedKey);
    });

    private final Function<String, String> key;

    ApiKey(Function<String, String> key) {
        this.key = key;
    }

    private static final String TEMPLATE_AUTH_KEY = "template-auth";
    private static final String TEMPLATE_CORE_KEY = "template-core";

    public static String getKey(String path, Map<String, String> keys) throws IllegalAccessException {
        if (path.contains(TEMPLATE_AUTH_KEY))
            return TEMPLATE_AUTH.run(keys.get(TEMPLATE_AUTH_KEY));

        if (path.contains(TEMPLATE_CORE_KEY))
            return TEMPLATE_CORE.run(keys.get(TEMPLATE_CORE_KEY));

        throw new IllegalAccessException(String.format("[API KEY] - No mapped path on request - [%s]", path));
    }

    private String run(String api) {
        return key.apply(api);
    }

}