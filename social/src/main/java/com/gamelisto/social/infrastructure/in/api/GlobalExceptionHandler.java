package com.gamelisto.social.infrastructure.in.api;
import com.gamelisto.social.application.exceptions.ApplicationException;
import com.gamelisto.social.dominio.exceptions.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
    log.warn("DomainException: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(errorBody(ex.getMessage(), 400));
  }
  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<Map<String, Object>> handleApplication(ApplicationException ex) {
    log.warn("ApplicationException: {}", ex.getMessage());
    return ResponseEntity.unprocessableEntity().body(errorBody(ex.getMessage(), 422));
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("IllegalArgumentException: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(errorBody(ex.getMessage(), 400));
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    log.error("Error inesperado: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorBody("Error interno del servidor", 500));
  }
  private Map<String, Object> errorBody(String message, int status) {
    return Map.of(
        "timestamp", Instant.now().toString(),
        "status", status,
        "error", message
    );
  }
}
