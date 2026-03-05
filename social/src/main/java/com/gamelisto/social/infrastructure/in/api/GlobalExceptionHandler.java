package com.gamelisto.social.infrastructure.in.api;

import java.time.Instant;
import java.util.Map;

import com.gamelisto.social.dominio.exceptions.DomainException;
import com.gamelisto.social.infrastructure.exceptions.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
    log.warn("DomainException: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorBody(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(InfrastructureException.class)
  public ResponseEntity<Map<String, Object>> handleInfrastructure(InfrastructureException ex) {
    log.warn("InfrastructureException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorBody(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
    int status = ex.getStatusCode().value();
    String message = (ex.getReason() != null) ? ex.getReason() : ex.getMessage();
    log.warn("ResponseStatusException {}: {}", status, message);
    return ResponseEntity.status(status).body(errorBody(message, status));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    log.error("Error inesperado: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorBody("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  private Map<String, Object> errorBody(String message, int status) {
    return Map.of(
        "timestamp", Instant.now().toString(),
        "status", status,
        "error", message);
  }
}
