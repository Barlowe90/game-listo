package com.gamelisto.usuarios_service.infrastructure.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gamelisto.usuarios_service.domain.exceptions.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Manejo centralizado de excepciones")
class GlobalExceptionHandlerTest {

  @InjectMocks private GlobalExceptionHandler handler;

  // ========== EXCEPCIONES 404 NOT FOUND ==========

  @Test
  @DisplayName("Debe manejar UsuarioNoEncontradoException con 404")
  void debeManejarUsuarioNoEncontradoException() {
    // Arrange
    String usuarioId = "123e4567-e89b-12d3-a456-426614174000";
    UsuarioNoEncontradoException exception = new UsuarioNoEncontradoException(usuarioId);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleUsuarioNoEncontrado(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(404);
    assertThat(response.getBody().get("error")).asString().contains("no encontrado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 401 UNAUTHORIZED ==========

  @Test
  @DisplayName("Debe manejar CredencialesInvalidasException con 401")
  void debeManejarCredencialesInvalidasException() {
    // Arrange
    CredencialesInvalidasException exception =
        new CredencialesInvalidasException("Credenciales inválidas");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleCredencialesInvalidas(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(401);
    assertThat(response.getBody().get("error")).asString().contains("inválidas");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar RefreshTokenInvalidoException con 401")
  void debeManejarRefreshTokenInvalidoException() {
    // Arrange
    RefreshTokenInvalidoException exception =
        new RefreshTokenInvalidoException("Token inválido o revocado");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleRefreshTokenInvalido(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(401);
    assertThat(response.getBody().get("error")).asString().contains("Token inválido");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar RefreshTokenExpiradoException con 401")
  void debeManejarRefreshTokenExpiradoException() {
    // Arrange
    RefreshTokenExpiradoException exception =
        new RefreshTokenExpiradoException("Refresh token expirado");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleRefreshTokenExpirado(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(401);
    assertThat(response.getBody().get("error")).asString().contains("expirado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 409 CONFLICT ==========

  @Test
  @DisplayName("Debe manejar EmailYaRegistradoException con 409")
  void debeManejarEmailYaRegistradoException() {
    // Arrange
    String email = "test@example.com";
    EmailYaRegistradoException exception = new EmailYaRegistradoException(email);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleEmailYaRegistrado(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(409);
    assertThat(response.getBody().get("error")).asString().containsIgnoringCase("email");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar UsernameYaExisteException con 409")
  void debeManejarUsernameYaExisteException() {
    // Arrange
    String username = "testuser";
    UsernameYaExisteException exception = new UsernameYaExisteException(username);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleUsernameYaExiste(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(409);
    assertThat(response.getBody().get("error")).asString().containsIgnoringCase("username");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar DiscordYaVinculadoException con 409")
  void debeManejarDiscordYaVinculadoException() {
    // Arrange
    DiscordYaVinculadoException exception =
        new DiscordYaVinculadoException("Discord ya vinculado a otro usuario");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleDiscordYaVinculado(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(409);
    assertThat(response.getBody().get("error")).asString().contains("Discord ya vinculado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar UsuarioYaVerificadoException con 409")
  void debeManejarUsuarioYaVerificadoException() {
    // Arrange
    String email = "test@example.com";
    UsuarioYaVerificadoException exception = new UsuarioYaVerificadoException(email);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleUsuarioYaVerificado(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(409);
    assertThat(response.getBody().get("error")).asString().containsIgnoringCase("verificado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 400 BAD REQUEST ==========

  @Test
  @DisplayName("Debe manejar MethodArgumentNotValidException con 400 y detalles de errores")
  void debeManejarMethodArgumentNotValidException() {
    // Arrange
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError1 = new FieldError("object", "username", "Username es requerido");
    FieldError fieldError2 = new FieldError("object", "email", "Email inválido");

    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).isEqualTo("Error de validación");
    assertThat(response.getBody().get("timestamp")).isNotNull();

    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
    assertThat(errors).isNotNull();
    assertThat(errors).hasSize(2);
    assertThat(errors.get("username")).isEqualTo("Username es requerido");
    assertThat(errors.get("email")).isEqualTo("Email inválido");
  }

  @Test
  @DisplayName("Debe manejar TokenVerificacionInvalidoException con 400")
  void debeManejarTokenVerificacionInvalidoException() {
    // Arrange
    TokenVerificacionInvalidoException exception =
        new TokenVerificacionInvalidoException("Token de verificación inválido o expirado");

    // Act
    ResponseEntity<Map<String, Object>> response =
        handler.handleTokenVerificacionInvalido(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).asString().containsIgnoringCase("token");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar IllegalArgumentException con 400")
  void debeManejarIllegalArgumentException() {
    // Arrange
    IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).asString().contains("Argumento inválido");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar IllegalStateException con 400")
  void debeManejarIllegalStateException() {
    // Arrange
    IllegalStateException exception = new IllegalStateException("Estado inválido");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).asString().contains("Estado inválido");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 403 FORBIDDEN ==========

  @Test
  @DisplayName("Debe manejar AccessDeniedException con 403")
  void debeManejarAccessDeniedException() {
    // Arrange
    AccessDeniedException exception = new AccessDeniedException("Acceso denegado");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(403);
    assertThat(response.getBody().get("error"))
        .asString()
        .contains("No tienes permisos suficientes");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 500 INTERNAL SERVER ERROR ==========

  @Test
  @DisplayName("Debe manejar Exception genérica con 500")
  void debeManejarExceptionGenerica() {
    // Arrange
    Exception exception = new Exception("Error inesperado");

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error")).asString().contains("Error interno del servidor");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar AlgoritmoNoEncontradoException con 500")
  void debeManejarAlgoritmoNoEncontradoException() {
    // Arrange
    AlgoritmoNoEncontradoException exception =
        new AlgoritmoNoEncontradoException("Algoritmo de encriptación no encontrado");

    // Act
    ResponseEntity<Map<String, Object>> response =
        handler.handleAlgoritmoNoEncontradoException(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error"))
        .asString()
        .contains("Algoritmo de encriptación no encontrado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar EventoPublicacionException con 500")
  void debeManejarEventoPublicacionException() {
    // Arrange
    EventoPublicacionException exception =
        new EventoPublicacionException("Error al publicar evento");

    // Act
    ResponseEntity<Map<String, Object>> response =
        handler.handleUsuarioPubliserException(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error")).asString().contains("Error al publicar evento");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  @Test
  @DisplayName("Debe manejar EventoListenerException con 500")
  void debeManejarEventoListenerException() {
    // Arrange
    EventoListenerException exception = new EventoListenerException("Error al escuchar evento");

    // Act
    ResponseEntity<Map<String, Object>> response =
        handler.handleUsuarioListenerException(exception);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error")).asString().contains("Error al escuchar evento");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== FORMATO DE RESPUESTA ==========

  @Test
  @DisplayName("Debe retornar formato JSON consistente con error, status y timestamp")
  void debeRetornarFormatoJsonConsistente() {
    // Arrange
    String usuarioId = "123e4567-e89b-12d3-a456-426614174000";
    UsuarioNoEncontradoException exception = new UsuarioNoEncontradoException(usuarioId);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleUsuarioNoEncontrado(exception);

    // Assert
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsKeys("error", "status", "timestamp");
    assertThat(response.getBody().get("error")).isNotNull();
    assertThat(response.getBody().get("status")).isInstanceOf(Integer.class);
    assertThat(response.getBody().get("timestamp")).isInstanceOf(String.class);
  }

  @Test
  @DisplayName("Debe retornar formato especial para errores de validación con detalles")
  void debeRetornarFormatoEspecialParaValidacion() {
    // Arrange
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "Error message");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    // Act
    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    // Assert
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsKeys("error", "errors", "status", "timestamp");
    assertThat(response.getBody().get("errors")).isInstanceOf(Map.class);
  }
}
