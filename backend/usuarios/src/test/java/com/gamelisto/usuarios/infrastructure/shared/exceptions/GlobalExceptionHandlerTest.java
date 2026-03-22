package com.gamelisto.usuarios.infrastructure.shared.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
import com.gamelisto.usuarios.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.usuarios.shared.exceptions.GlobalExceptionHandler;
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

  // ========== DOMAINEXCEPTION - 400 BAD REQUEST ==========

  @Test
  @DisplayName("Debe manejar DomainException con 400")
  void debeManejarDomainException() {
    DomainException exception = new DomainException("Regla de negocio violada");

    ResponseEntity<Map<String, Object>> response = handler.handleDomainException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).isEqualTo("Regla de negocio violada");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== APPLICATIONEXCEPTION - 422 UNPROCESSABLE ENTITY ==========

  @Test
  @DisplayName("Debe manejar ApplicationException con 422")
  void debeManejarApplicationException() {
    ApplicationException exception = new ApplicationException("Recurso no encontrado");

    ResponseEntity<Map<String, Object>> response = handler.handleApplicationException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(422);
    assertThat(response.getBody().get("error")).isEqualTo("Recurso no encontrado");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== INFRASTRUCTUREEXCEPTION - 500 INTERNAL SERVER ERROR ==========

  @Test
  @DisplayName("Debe manejar InfrastructureException con 500")
  void debeManejarInfrastructureException() {
    InfrastructureException exception = new InfrastructureException("Error al enviar email");

    ResponseEntity<Map<String, Object>> response = handler.handleInfrastructureException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error")).isEqualTo("Error al enviar email");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== EXCEPCIONES 403 FORBIDDEN ==========

  @Test
  @DisplayName("Debe manejar AccessDeniedException con 403")
  void debeManejarAccessDeniedException() {
    AccessDeniedException exception = new AccessDeniedException("Acceso denegado");

    ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(403);
    assertThat(response.getBody().get("error"))
        .asString()
        .contains("No tienes permisos suficientes");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== VALIDACIÃ“N - 400 BAD REQUEST ==========

  @Test
  @DisplayName("Debe manejar MethodArgumentNotValidException con 400 y detalles de errores")
  void debeManejarMethodArgumentNotValidException() {
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError1 = new FieldError("object", "username", "Username es requerido");
    FieldError fieldError2 = new FieldError("object", "email", "Email inválido");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(400);
    assertThat(response.getBody().get("error")).isEqualTo("Error de validación");
    assertThat(response.getBody().get("timestamp")).isNotNull();

    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
    assertThat(errors).hasSize(2);
    assertThat(errors.get("username")).isEqualTo("Username es requerido");
    assertThat(errors.get("email")).isEqualTo("Email inválido");
  }

  // ========== GENÃ‰RICO - 500 INTERNAL SERVER ERROR ==========

  @Test
  @DisplayName("Debe manejar Exception genÃ©rica con 500")
  void debeManejarExceptionGenerica() {
    Exception exception = new Exception("Error inesperado");

    ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status")).isEqualTo(500);
    assertThat(response.getBody().get("error")).asString().contains("Error interno del servidor");
    assertThat(response.getBody().get("timestamp")).isNotNull();
  }

  // ========== FORMATO DE RESPUESTA ==========

  @Test
  @DisplayName("Debe retornar formato JSON consistente con error, status y timestamp")
  void debeRetornarFormatoJsonConsistente() {
    DomainException exception = new DomainException("Mensaje de error");

    ResponseEntity<Map<String, Object>> response = handler.handleDomainException(exception);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsKeys("error", "status", "timestamp");
    assertThat(response.getBody().get("error")).isNotNull();
    assertThat(response.getBody().get("status")).isInstanceOf(Integer.class);
    assertThat(response.getBody().get("timestamp")).isInstanceOf(String.class);
  }

  @Test
  @DisplayName("Debe retornar formato especial para errores de validaciÃ³n con detalles")
  void debeRetornarFormatoEspecialParaValidacion() {
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "Error message");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsKeys("error", "errors", "status", "timestamp");
    assertThat(response.getBody().get("errors")).isInstanceOf(Map.class);
  }
}



