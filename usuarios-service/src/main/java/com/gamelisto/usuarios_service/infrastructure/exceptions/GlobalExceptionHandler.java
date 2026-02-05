package com.gamelisto.usuarios_service.infrastructure.exceptions;

import com.gamelisto.usuarios_service.domain.exceptions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.gamelisto.usuarios_service.domain.usuario.Email;
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

  // ============ Excepciones de Negocio - 404 Not Found ============

  @ExceptionHandler(UsuarioNoEncontradoException.class)
  public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(
      UsuarioNoEncontradoException ex) {
    logger.warn("Usuario no encontrado: {}", ex.getUsuarioId());
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // ============ Excepciones de Negocio - 409 Conflict ============

  @ExceptionHandler(UsernameYaExisteException.class)
  public ResponseEntity<Map<String, Object>> handleUsernameYaExiste(UsernameYaExisteException ex) {
    logger.warn("Username ya existe: {}", ex.getUsername());
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(EmailYaRegistradoException.class)
  public ResponseEntity<Map<String, Object>> handleEmailYaRegistrado(
      EmailYaRegistradoException ex) {
    logger.warn("Email ya registrado: {}", ex.getEmail());
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(DiscordYaVinculadoException.class)
  public ResponseEntity<Map<String, Object>> handleDiscordYaVinculado(
      DiscordYaVinculadoException ex) {
    logger.warn("Discord ya vinculado: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UsuarioYaVerificadoException.class)
  public ResponseEntity<Map<String, Object>> handleUsuarioYaVerificado(
      UsuarioYaVerificadoException ex) {
    logger.warn("Usuario ya verificado: {}", ex.getEmail());
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UsuarioNoActivoException.class)
  public ResponseEntity<Map<String, Object>> handleUsuarioNoActivo(UsuarioNoActivoException ex) {
    logger.warn("Usuario no activo: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  // ============ Excepciones de Autenticación - 401 Unauthorized ============

  @ExceptionHandler(RefreshTokenInvalidoException.class)
  public ResponseEntity<Map<String, Object>> handleRefreshTokenInvalido(
      RefreshTokenInvalidoException ex) {
    logger.warn("Refresh token inválido o revocado: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(RefreshTokenExpiradoException.class)
  public ResponseEntity<Map<String, Object>> handleRefreshTokenExpirado(
      RefreshTokenExpiradoException ex) {
    logger.warn("Refresh token expirado: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(CredencialesInvalidasException.class)
  public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(
      CredencialesInvalidasException ex) {
    logger.warn("Credenciales inválidas");
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  // ============ Excepciones de Autorización - 403 Forbidden ============

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    logger.warn("Acceso denegado: {}", ex.getMessage());
    return buildErrorResponse(
        "No tienes permisos suficientes para realizar esta operación", HttpStatus.FORBIDDEN);
  }

  // ============ Excepciones de Validación - 400 Bad Request ============

  @ExceptionHandler(TokenVerificacionInvalidoException.class)
  public ResponseEntity<Map<String, Object>> handleTokenVerificacionInvalido(
      TokenVerificacionInvalidoException ex) {
    logger.warn("Token de verificación inválido o expirado");
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    logger.warn("Argumento inválido: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
    logger.warn("Estado inválido: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

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

  // ============ Excepciones Genéricas - 500 Internal Server Error ============

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    logger.error("Error inesperado: {}", ex.getMessage(), ex);
    return buildErrorResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AlgoritmoNoEncontradoException.class)
  public ResponseEntity<Map<String, Object>> handleAlgoritmoNoEncontradoException(
      AlgoritmoNoEncontradoException ex) {
    logger.warn("Error al seleccionar algoritmo de encriptacion: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EventoPublicacionException.class)
  public ResponseEntity<Map<String, Object>> handleUsuarioPubliserException(
      EventoPublicacionException ex) {
    logger.warn("Error al publicar evento: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EventoListenerException.class)
  public ResponseEntity<Map<String, Object>> handleUsuarioListenerException(
      EventoListenerException ex) {
    logger.warn("Error al escuchar evento: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<Map<String, Object>> handleEmailSendingException(EmailSendingException ex) {
    logger.warn("Error al enviar el email: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

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
