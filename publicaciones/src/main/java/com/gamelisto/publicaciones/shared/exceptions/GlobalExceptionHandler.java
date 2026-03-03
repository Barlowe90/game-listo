package com.gamelisto.publicaciones.shared.exceptions;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import com.gamelisto.publicaciones.infrastructure.exceptions.InfrastructureException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ============ Capa de Dominio - 400 Bad Request ============

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
    logger.warn("Error de domain: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // ============ Capa de Aplicación - 422 Unprocessable Entity ============

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
    logger.warn("Error de aplicación: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
  }

  // ============ Capa de Infraestructura - 500 Internal Server Error ============

  @ExceptionHandler(InfrastructureException.class)
  public ResponseEntity<Map<String, Object>> handleInfrastructureException(
      InfrastructureException ex) {
    logger.error("Error de infraestructura: {}", ex.getMessage(), ex);
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // ============ Helper ============

  private ResponseEntity<Map<String, Object>> buildErrorResponse(
      String message, HttpStatus status) {
    Map<String, Object> error = new HashMap<>();
    error.put("error", message);
    error.put("status", status.value());
    error.put("timestamp", Instant.now().toString());
    return ResponseEntity.status(status).body(error);
  }
}
