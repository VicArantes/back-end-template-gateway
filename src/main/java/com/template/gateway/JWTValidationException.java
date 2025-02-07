package com.template.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JWTValidationException extends RuntimeException {

    public JWTValidationException(String message) {
        super(message);
    }

}