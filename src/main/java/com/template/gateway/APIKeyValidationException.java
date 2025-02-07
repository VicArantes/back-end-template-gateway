package com.template.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class APIKeyValidationException extends RuntimeException {

    public APIKeyValidationException(String message) {
        super(message);
    }

}