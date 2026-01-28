package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminarUsuarioUseCaseTest {

    @Mock
    private RepositorioUsuarios repositorioUsuarios;

    @Mock
    private IUsuarioPublisher eventosPublisher;

    @InjectMocks
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.create(
                Username.of("testuser"),
                Email.of("test@test.com"),
                PasswordHash.of("$2a$10$hash"));

        // Activar usuario
        usuario.verificarEmail(usuario.getTokenVerificacion());
    }

    @Test
    @DisplayName("Debe eliminar usuario exitosamente (soft delete)")
    void debeEliminarUsuarioExitosamente() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios).save(argThat(u -> u.getStatus() == EstadoUsuario.ELIMINADO));
        verify(eventosPublisher).publish(eq("usuario.eliminado"), any(UsuarioEliminado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioId = "00000000-0000-0000-0000-000000000000";

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class,
                () -> eliminarUsuarioUseCase.execute(usuarioId));

        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
        verify(eventosPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("Debe cambiar estado a ELIMINADO")
    void debeCambiarEstadoAEliminado() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();
        EstadoUsuario estadoAnterior = usuario.getStatus();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario savedUsuario = invocation.getArgument(0);
                    assertEquals(EstadoUsuario.ELIMINADO, savedUsuario.getStatus());
                    return savedUsuario;
                });

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert
        assertEquals(EstadoUsuario.ACTIVO, estadoAnterior);
        verify(repositorioUsuarios).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe publicar evento UsuarioEliminado")
    void debePublicarEventoUsuarioEliminado() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<UsuarioEliminado> eventoCaptor = ArgumentCaptor.forClass(UsuarioEliminado.class);

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert
        verify(eventosPublisher).publish(eq("usuario.eliminado"), eventoCaptor.capture());

        UsuarioEliminado evento = eventoCaptor.getValue();
        assertNotNull(evento);
        assertEquals(usuarioId, evento.usuarioId());
        assertNotNull(evento.occurredOn());
    }

    @Test
    @DisplayName("Debe preservar datos del usuario tras eliminación (soft delete)")
    void debePreservarDatosDelUsuario() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();
        String usernameOriginal = usuario.getUsername().value();
        String emailOriginal = usuario.getEmail().value();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario savedUsuario = invocation.getArgument(0);
                    assertEquals(usernameOriginal, savedUsuario.getUsername().value());
                    assertEquals(emailOriginal, savedUsuario.getEmail().value());
                    return savedUsuario;
                });

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert
        verify(repositorioUsuarios).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe eliminar usuario en cualquier estado")
    void debeEliminarUsuarioEnCualquierEstado() {
        // Arrange - Probar con diferentes estados
        EstadoUsuario[] estados = {
                EstadoUsuario.PENDIENTE_DE_VERIFICACION,
                EstadoUsuario.ACTIVO,
                EstadoUsuario.SUSPENDIDO
        };

        for (EstadoUsuario estado : estados) {
            Usuario usuarioPrueba = Usuario.create(
                    Username.of("user" + estado.name()),
                    Email.of("user" + estado.name() + "@test.com"),
                    PasswordHash.of("$2a$10$hash"));

            if (estado == EstadoUsuario.ACTIVO) {
                usuarioPrueba.verificarEmail(usuarioPrueba.getTokenVerificacion());
            } else if (estado == EstadoUsuario.SUSPENDIDO) {
                usuarioPrueba.verificarEmail(usuarioPrueba.getTokenVerificacion());
                usuarioPrueba.suspend();
            }

            String id = usuarioPrueba.getId().value().toString();

            when(repositorioUsuarios.findById(any(UsuarioId.class)))
                    .thenReturn(Optional.of(usuarioPrueba));
            when(repositorioUsuarios.save(any(Usuario.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            eliminarUsuarioUseCase.execute(id);

            // Assert
            verify(repositorioUsuarios, atLeastOnce()).save(argThat(u -> u.getStatus() == EstadoUsuario.ELIMINADO));
        }
    }

    @Test
    @DisplayName("Debe lanzar excepción si ID de usuario es inválido")
    void debeLanzarExcepcionSiIdInvalido() {
        // Arrange
        String usuarioIdInvalido = "id-invalido";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> eliminarUsuarioUseCase.execute(usuarioIdInvalido));

        verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
        verify(eventosPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("Debe actualizar timestamp de updatedAt al eliminar")
    void debeActualizarTimestampAlEliminar() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario savedUsuario = invocation.getArgument(0);
                    assertTrue(savedUsuario.getUpdatedAt().isAfter(savedUsuario.getCreatedAt()));
                    return savedUsuario;
                });

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert
        verify(repositorioUsuarios).save(any(Usuario.class));
    }

    @Test
    @DisplayName("No debe eliminar físicamente el usuario (hard delete)")
    void noDebeEliminarFisicamente() {
        // Arrange
        String usuarioId = usuario.getId().value().toString();

        when(repositorioUsuarios.findById(any(UsuarioId.class)))
                .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        eliminarUsuarioUseCase.execute(usuarioId);

        // Assert - Solo debe guardar el usuario con estado ELIMINADO, no hacer hard
        // delete
        verify(repositorioUsuarios).save(any(Usuario.class));
        verify(repositorioUsuarios, times(1)).save(any(Usuario.class));
        verify(repositorioUsuarios, times(1)).findById(any(UsuarioId.class));
    }
}
