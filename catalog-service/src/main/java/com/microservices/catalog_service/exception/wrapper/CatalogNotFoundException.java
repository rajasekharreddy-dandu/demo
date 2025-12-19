package com.microservices.catalog_service.exception.wrapper;

import java.io.Serial;

public class CatalogNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CatalogNotFoundException() {
        super();
    }

    public CatalogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CatalogNotFoundException(String message) {
        super(message);
    }

    public CatalogNotFoundException(Throwable cause) {
        super(cause);
    }
}

