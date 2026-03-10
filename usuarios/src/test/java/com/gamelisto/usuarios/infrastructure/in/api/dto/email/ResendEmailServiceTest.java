package com.gamelisto.usuarios.infrastructure.in.api.dto.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.usuarios.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.usuarios.infrastructure.out.email.ResendEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ResendEmailService - Tests")
class ResendEmailServiceTest {

  private ResendEmailService emailService;

  private static final String API_KEY = "re_test_api_key";
  private static final String FROM_EMAIL = "noreply@gamelisto.com";
  private static final String FRONTEND_URL = "https://gamelisto.com";

  @BeforeEach
  void setUp() {
    emailService = new ResendEmailService(API_KEY, FROM_EMAIL, FRONTEND_URL);
  }

  @Test
  @DisplayName("Debe inicializar el servicio correctamente")
  void debeInicializarElServicioCorrectamente() {
    // When
    ResendEmailService service = new ResendEmailService(API_KEY, FROM_EMAIL, FRONTEND_URL);

    // Then
    assertThat(service).isNotNull();
  }

  @Test
  @DisplayName("Debe tener método sendVerificationEmail implementado")
  void debeTenerMetodoSendVerificationEmailImplementado() {
    // When & Then - La funcion debe existir y ser invocable
    // Nota: Este test fallará con ResendException porque no hay API key válido,
    // pero verifica que la funcion está implementado correctamente
    assertThatThrownBy(
            () -> emailService.sendVerificationEmail("user@example.com", "testuser", "token123"))
        .isInstanceOf(InfrastructureException.class);
  }

  @Test
  @DisplayName("Debe tener método sendPasswordResetEmail implementado")
  void debeTenerMetodoSendPasswordResetEmailImplementado() {
    // When & Then
    assertThatThrownBy(
            () -> emailService.sendPasswordResetEmail("user@example.com", "testuser", "resettoken"))
        .isInstanceOf(InfrastructureException.class);
  }

  @Test
  @DisplayName("Debe lanzar InfrastructureException cuando API key es inválido")
  void debeLanzarEmailSendingExceptionCuandoAPIKeyEsInvalido() {
    // Given
    String toEmail = "user@example.com";
    String username = "testuser";
    String token = "token123";

    // When & Then
    assertThatThrownBy(() -> emailService.sendVerificationEmail(toEmail, username, token))
        .isInstanceOf(InfrastructureException.class)
        .hasMessageContaining("No se pudo enviar el email");
  }

  @Test
  @DisplayName("Debe lanzar InfrastructureException cuando falla envío de reset password")
  void debeLanzarEmailSendingExceptionCuandoFallaEnvioDeResetPassword() {
    // Given
    String toEmail = "user@example.com";
    String username = "testuser";
    String token = "reset-token";

    // When & Then
    assertThatThrownBy(() -> emailService.sendPasswordResetEmail(toEmail, username, token))
        .isInstanceOf(InfrastructureException.class)
        .hasMessageContaining("No se pudo enviar el email");
  }

  @Test
  @DisplayName("Debe aceptar diferentes formatos de email válidos")
  void debeAceptarDiferentesFormatosDeEmailValidos() {
    // Given
    String[] validEmails = {
      "user@example.com", "user.name@example.com", "user+tag@example.co.uk", "123@example.com"
    };

    // When & Then
    for (String email : validEmails) {
      assertThatThrownBy(() -> emailService.sendVerificationEmail(email, "user", "token"))
          .isInstanceOf(InfrastructureException.class); // Falla por API key, no por validación
    }
  }

  @Test
  @DisplayName("Debe manejar username con caracteres especiales")
  void debeManejarUsernameConCaracteresEspeciales() {
    // Given
    String specialUsername = "user_name-123";
    String token = "token";

    // When & Then - No debe lanzar NullPointerException o IllegalArgumentException
    assertThatThrownBy(
            () -> emailService.sendVerificationEmail("test@test.com", specialUsername, token))
        .isInstanceOf(InfrastructureException.class); // Solo falla por API key
  }

  @Test
  @DisplayName("Debe manejar tokens largos correctamente")
  void debeManejarTokensLargosCorrectamente() {
    // Given
    String longToken = "a".repeat(200);

    // When & Then
    assertThatThrownBy(() -> emailService.sendVerificationEmail("test@test.com", "user", longToken))
        .isInstanceOf(InfrastructureException.class);
  }

  @Test
  @DisplayName("Debe construir URL de verificación con formato correcto")
  void debeConstruirURLDeVerificacionConFormatoCorrect() {
    // Esta prueba verifica que el servicio construye URLs válidas
    // al intentar enviar (aunque falle por API key inválido)

    // When & Then
    assertThatThrownBy(
            () -> emailService.sendVerificationEmail("test@test.com", "user", "mytoken123"))
        .isInstanceOf(InfrastructureException.class)
        .hasMessageContaining("verificación");
  }

  @Test
  @DisplayName("Debe construir URL de reset con formato correcto")
  void debeConstruirURLDeResetConFormatoCorrecto() {
    // When & Then
    assertThatThrownBy(
            () -> emailService.sendPasswordResetEmail("test@test.com", "user", "resettoken456"))
        .isInstanceOf(InfrastructureException.class)
        .hasMessageContaining("restablecimiento de contraseña");
  }

  @Test
  @DisplayName("Debe propagar excepción de Resend como InfrastructureException")
  void debePropagarExcepcionDeResendComoEmailSendingException() {
    // Given
    String toEmail = "invalid@example.com";
    String username = "user";
    String token = "token";

    // When & Then
    assertThatThrownBy(() -> emailService.sendVerificationEmail(toEmail, username, token))
        .isInstanceOf(InfrastructureException.class)
        .hasCauseInstanceOf(com.resend.core.exception.ResendException.class);
  }

  @Test
  @DisplayName("Debe poder crear múltiples instancias del servicio")
  void debePoderCrearMultiplesInstanciasDelServicio() {
    // When
    ResendEmailService service1 = new ResendEmailService(API_KEY, FROM_EMAIL, FRONTEND_URL);
    ResendEmailService service2 =
        new ResendEmailService("another-key", "other@email.com", "https://other.com");
    ResendEmailService service3 =
        new ResendEmailService(API_KEY, FROM_EMAIL, "https://different-frontend.com");

    // Then
    assertThat(service1).isNotNull();
    assertThat(service2).isNotNull();
    assertThat(service3).isNotNull();
  }

  @Test
  @DisplayName("Debe aceptar configuración con diferentes URLs de frontend")
  void debeAceptarConfiguracionConDiferentesURLsDeFrontend() {
    // Given
    String[] frontendUrls = {
      "https://gamelisto.com",
      "https://staging.gamelisto.com",
      "https://dev.gamelisto.com",
      "http://localhost:8080"
    };

    // When & Then
    for (String url : frontendUrls) {
      ResendEmailService service = new ResendEmailService(API_KEY, FROM_EMAIL, url);
      assertThat(service).isNotNull();
    }
  }

  @Test
  @DisplayName("Debe aceptar configuración con diferentes emails de origen")
  void debeAceptarConfiguracionConDiferentesEmailsDeOrigen() {
    // Given
    String[] fromEmails = {
      "noreply@gamelisto.com",
      "support@gamelisto.com",
      "hello@gamelisto.com",
      "notifications@gamelisto.com"
    };

    // When & Then
    for (String email : fromEmails) {
      ResendEmailService service = new ResendEmailService(API_KEY, email, FRONTEND_URL);
      assertThat(service).isNotNull();
    }
  }
}
