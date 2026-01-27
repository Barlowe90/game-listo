package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.CambiarContrasenaCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CambiarContrasenaUseCase - Tests")
class CambiarContrasenaUseCaseTest {

    @Mock
    private RepositorioUsuarios repositorioUsuarios;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CambiarContrasenaUseCase cambiarContrasenaUseCase;

    // ========== CASOS DE ÉXITO ==========

    @Test
    @DisplayName("Debe cambiar contraseña exitosamente cuando la actual es correcta")
    void debeCambiarContrasenaExitosamente() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String contrasenaActual = "password123";
        String contrasenaNueva = "newPassword456";
        String hashActual = "$2a$10$hashActual";
        String hashNuevo = "$2a$10$hashNuevo";

        Usuario usuario = crearUsuarioConPassword(usuarioId, hashActual);

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(contrasenaActual, hashActual))
                .thenReturn(true);
        when(passwordEncoder.encode(contrasenaNueva))
                .thenReturn(hashNuevo);
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CambiarContrasenaCommand command = new CambiarContrasenaCommand(
                usuarioId, contrasenaActual, contrasenaNueva);

        // Act
        cambiarContrasenaUseCase.execute(command);

        // Assert
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(passwordEncoder).matches(contrasenaActual, hashActual);
        verify(passwordEncoder).encode(contrasenaNueva);
        verify(repositorioUsuarios).save(argThat(u -> u.getPasswordHash().value().equals(hashNuevo)));
    }

    @Test
    @DisplayName("Debe hashear la nueva contraseña antes de guardar")
    void debeHashearNuevaContrasenaAntesDeGuardar() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String hashActual = "$2a$10$hashActual";
        String hashNuevo = "$2a$10$newEncodedPassword";

        Usuario usuario = crearUsuarioConPassword(usuarioId, hashActual);

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), eq(hashActual)))
                .thenReturn(true);
        when(passwordEncoder.encode("nuevaContrasena"))
                .thenReturn(hashNuevo);
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CambiarContrasenaCommand command = new CambiarContrasenaCommand(
                usuarioId, "actual", "nuevaContrasena");

        // Act
        cambiarContrasenaUseCase.execute(command);

        // Assert
        verify(passwordEncoder).encode("nuevaContrasena");
        assertEquals(hashNuevo, usuario.getPasswordHash().value());
    }

    // ========== CASOS DE ERROR ==========

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdInexistente = UUID.randomUUID().toString();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.empty());

        CambiarContrasenaCommand command = new CambiarContrasenaCommand(
                usuarioIdInexistente, "password", "newPassword");

        // Act & Assert
        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> cambiarContrasenaUseCase.execute(command));

        verify(repositorioUsuarios, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña actual es incorrecta")
    void debeLanzarExcepcionSiContrasenaActualEsIncorrecta() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String hashActual = "$2a$10$hashActual";

        Usuario usuario = crearUsuarioConPassword(usuarioId, hashActual);

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("contrasenaIncorrecta", hashActual))
                .thenReturn(false);

        CambiarContrasenaCommand command = new CambiarContrasenaCommand(
                usuarioId, "contrasenaIncorrecta", "newPassword");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cambiarContrasenaUseCase.execute(command));

        assertTrue(exception.getMessage().contains("contraseña actual no es correcta"));
        verify(repositorioUsuarios, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Usuario crearUsuarioConPassword(String id, String passwordHash) {
        return Usuario.reconstitute(
                UsuarioId.fromString(id),
                Username.of("testuser"),
                Email.of("test@test.com"),
                PasswordHash.of(passwordHash),
                Avatar.empty(),
                Instant.now(),
                Instant.now(),
                Rol.USER,
                Idioma.ESP,
                true,
                EstadoUsuario.ACTIVO,
                DiscordUserId.empty(),
                DiscordUsername.empty(),
                null,
                TokenVerificacion.empty(),
                null,
                TokenVerificacion.empty(),
                null);
    }
}
