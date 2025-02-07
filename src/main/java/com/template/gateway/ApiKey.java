package com.template.gateway;

import java.util.Base64;
import java.util.Map;
import java.util.function.Function;

public enum ApiKey {
    AUTH((key) -> {
        byte[] encodedKey = Base64.getEncoder().encode(key.getBytes());
        return new String(encodedKey);
    }),
    CORE((key) -> {
        byte[] encodedKey = Base64.getEncoder().encode(key.getBytes());
        return new String(encodedKey);
    });

    private final Function<String, String> key;

    ApiKey(Function<String, String> key) {
        this.key = key;
    }

    private static final String AUTH_KEY = "template-auth";
    private static final String CORE_KEY = "template-core";

    public static String getKey(String path, Map<String, String> keys) throws IllegalAccessException {
        if (path.contains(AUTH_KEY))
            return AUTH.run(keys.get(AUTH_KEY));

        if (path.contains(CORE_KEY))
            return CORE.run(keys.get(CORE_KEY));

        throw new IllegalAccessException(String.format("[API KEY] - No mapped path on request - [%s]", path));
    }

    private String run(String api) {
        return key.apply(api);
    }

}