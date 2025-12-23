package com.microservices.identity_service.exception.wrapper;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException() {
        super();
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(Throwable cause) {
        super(cause);
    }
}