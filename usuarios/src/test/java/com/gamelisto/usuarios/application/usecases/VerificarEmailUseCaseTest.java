package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerificarEmailUseCase - Tests")
class VerificarEmailUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;
  @Mock private IUsuarioPublisher eventosPublisher; // no eliminar ya que lo usa mockito
  @InjectMocks private VerificarEmailUseCase verificarEmailUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe verificar email exitosamente con token válido")
  void debeVerificarEmailExitosamente() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.generate();
    Usuario usuario = crearUsuarioPendiente(token, Instant.now().plusSeconds(3600));

    when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
        .thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    verificarEmailUseCase.execute(new VerificarEmailCommand(token.value()));

    // Assert
    assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    assertTrue(usuario.getTokenVerificacion().isEmpty());
    verify(repositorioUsuarios).save(usuario);
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si token no existe")
  void debeLanzarExcepcionSiTokenNoExiste() {
    // Arrange
    when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ApplicationException.class,
        () -> verificarEmailUseCase.execute(new VerificarEmailCommand("token_inexistente")));

    verify(repositorioUsuarios, never()).save(any());
  }

  // ========== HELPER METHODS ==========

  private Usuario crearUsuarioPendiente(TokenVerificacion token, Instant expiracion) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Rol.USER,
        Idioma.ESP,
        EstadoUsuario.PENDIENTE_DE_VERIFICACION,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        token,
        expiracion,
        TokenVerificacion.empty(),
        null);
  }
}
