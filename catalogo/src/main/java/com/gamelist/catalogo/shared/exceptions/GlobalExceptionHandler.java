package com.gamelist.catalogo.shared.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.gamelist.catalogo.application.exceptions.ApplicationException;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import com.gamelist.catalogo.infrastructure.exceptions.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  // ============ Autorización - 403 Forbidden ============

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    logger.warn("Acceso denegado: {}", ex.getMessage());
    return buildErrorResponse(
        "No tienes permisos suficientes para realizar esta operación", HttpStatus.FORBIDDEN);
  }

  // ============ Validación de Request - 400 Bad Request ============

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    logger.warn("Error de validación en request");

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    Map<String, Object> response = new HashMap<>();
    response.put("error", "Error de validación");
    response.put("errors", errors);
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("timestamp", Instant.now().toString());

    return ResponseEntity.badRequest().body(response);
  }

  // ============ Genérico - 500 Internal Server Error ============

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    logger.error("Error inesperado: {}", ex.getMessage(), ex);
    return buildErrorResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
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
