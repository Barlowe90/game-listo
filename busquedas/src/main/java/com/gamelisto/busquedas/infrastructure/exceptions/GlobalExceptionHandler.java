package com.gamelisto.busquedas.infrastructure.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(OpenSearchUnavailableException.class)
  public ResponseEntity<Map<String, String>> handleOpenSearchUnavailable(
      OpenSearchUnavailableException ex) {
    logger.error("OpenSearch no disponible: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of("error", "Servicio de busqueda no disponible. Intentelo mas tarde."));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, String>> handleMissingParam(
      MissingServletRequestParameterException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("error", "Parametro requerido: " + ex.getParameterName()));
  }
}
