package com.gamelisto.usuarios_service.infrastructure.exceptions;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message
) {
    public ErrorResponse(String error, String message) {
        this(Instant.now(), 0, error, message);
    }
    
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message);
    }
}
