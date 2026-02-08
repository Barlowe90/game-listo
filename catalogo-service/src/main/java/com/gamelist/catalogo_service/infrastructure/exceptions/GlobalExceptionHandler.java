package com.gamelist.catalogo_service.infrastructure.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.gamelist.catalogo_service.domain.exceptions.GameNotFoundException;
import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import com.gamelist.catalogo_service.domain.exceptions.SyncStateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ============ Excepciones de Negocio - 404 Not Found ============

  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleGameNotFound(GameNotFoundException ex) {
    logger.warn("Videojuego no encontrado: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SyncStateNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleGameNotFound(SyncStateNotFoundException ex) {
    logger.warn("Estado de sincronización no encontrado: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // ============ Excepciones de Negocio - 409 Conflict ============

  // ============ Excepciones de Autenticación - 401 Unauthorized ============

  // ============ Excepciones de Autorización - 403 Forbidden ============

  // ============ Excepciones de Validación - 400 Bad Request ============

  @ExceptionHandler(InvalidGameDataException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidGameData(InvalidGameDataException ex) {
    logger.warn("Datos introducidos inválidos");
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // ============ Excepciones Genéricas - 500 Internal Server Error ============

  // ============ Helper Methods ============

  private ResponseEntity<Map<String, Object>> buildErrorResponse(
      String message, HttpStatus status) {
    Map<String, Object> error = new HashMap<>();
    error.put("error", message);
    error.put("status", status.value());
    error.put("timestamp", Instant.now().toString());
    return ResponseEntity.status(status).body(error);
  }
}
