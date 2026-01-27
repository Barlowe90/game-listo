package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.CambiarCorreoCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.domain.exceptions.EmailYaRegistradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CambiarCorreoUseCase - Tests")
class CambiarCorreoUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private CambiarCorreoUseCase cambiarCorreoUseCase;
    
    // ========== CASOS DE ÉXITO ==========
    
    @Test
    @DisplayName("Debe cambiar correo exitosamente cuando el nuevo email no está registrado")
    void debeCambiarCorreoExitosamente() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String emailActual = "usuario@ejemplo.com";
        String emailNuevo = "nuevo@ejemplo.com";
        
        Usuario usuario = crearUsuarioActivo(usuarioId, emailActual);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.empty());
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, emailNuevo);
        
        // Act
        cambiarCorreoUseCase.execute(command);
        
        // Assert
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios).findByEmail(any(Email.class));
        verify(repositorioUsuarios).save(argThat(u -> 
            u.getEmail().value().equals(emailNuevo) &&
            u.getStatus() == EstadoUsuario.PENDIENTE_DE_VERIFICACION &&
            u.getTokenVerificacion() != null &&
            !u.getTokenVerificacion().isEmpty()
        ));
    }
    
    @Test
    @DisplayName("Debe cambiar estado a PENDIENTE_DE_VERIFICACION al cambiar email")
    void debeCambiarEstadoAPendienteDeVerificacion() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        Usuario usuario = crearUsuarioActivo(usuarioId, "original@ejemplo.com");
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.empty());
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, "nuevo@ejemplo.com");
        
        // Act
        cambiarCorreoUseCase.execute(command);
        
        // Assert
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
    }
    
    @Test
    @DisplayName("Debe generar nuevo token de verificación al cambiar email")
    void debeGenerarNuevoTokenDeVerificacion() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        Usuario usuario = crearUsuarioActivo(usuarioId, "original@ejemplo.com");
        TokenVerificacion tokenOriginal = usuario.getTokenVerificacion();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.empty());
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, "nuevo@ejemplo.com");
        
        // Act
        cambiarCorreoUseCase.execute(command);
        
        // Assert
        assertNotNull(usuario.getTokenVerificacion());
        assertFalse(usuario.getTokenVerificacion().isEmpty());
        // El token debería ser diferente si el original estaba vacío o era diferente
        assertNotEquals(tokenOriginal, usuario.getTokenVerificacion());
    }
    
    @Test
    @DisplayName("No debe hacer nada si el nuevo email es igual al actual")
    void noDebeHacerNadaSiEmailEsIgual() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String emailActual = "mismo@ejemplo.com";
        
        Usuario usuario = crearUsuarioActivo(usuarioId, emailActual);
        EstadoUsuario estadoOriginal = usuario.getStatus();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        // El email es el mismo, así que findByEmail devuelve el mismo usuario
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, emailActual);
        
        // Act
        cambiarCorreoUseCase.execute(command);
        
        // Assert
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
        assertEquals(estadoOriginal, usuario.getStatus());
    }
    
    // ========== CASOS DE ERROR ==========
    
    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdInexistente = UUID.randomUUID().toString();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.empty());
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(
            usuarioIdInexistente, "nuevo@ejemplo.com"
        );
        
        // Act & Assert
        UsuarioNoEncontradoException exception = assertThrows(
            UsuarioNoEncontradoException.class,
            () -> cambiarCorreoUseCase.execute(command)
        );
        
        assertTrue(exception.getMessage().contains(usuarioIdInexistente));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el email ya está registrado por otro usuario")
    void debeLanzarExcepcionSiEmailYaRegistrado() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        String otroUsuarioId = UUID.randomUUID().toString();
        String emailDuplicado = "duplicado@ejemplo.com";
        
        Usuario usuario = crearUsuarioActivo(usuarioId, "original@ejemplo.com");
        Usuario otroUsuario = crearUsuarioActivo(otroUsuarioId, emailDuplicado);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(otroUsuario));
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, emailDuplicado);
        
        // Act & Assert
        EmailYaRegistradoException exception = assertThrows(
            EmailYaRegistradoException.class,
            () -> cambiarCorreoUseCase.execute(command)
        );
        
        assertTrue(exception.getMessage().contains(emailDuplicado));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción con formato de email inválido")
    void debeLanzarExcepcionConEmailInvalido() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        
        CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, "email-invalido");
        
        // Act & Assert - La excepción se lanza al crear el Email Value Object
        assertThrows(
            IllegalArgumentException.class,
            () -> cambiarCorreoUseCase.execute(command)
        );
        
        verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
    
    // ========== HELPERS ==========
    
    private Usuario crearUsuarioActivo(String id, String email) {
        Usuario usuario = Usuario.reconstitute(
            UsuarioId.fromString(id),
            Username.of("testuser"),
            Email.of(email),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.empty(),
            Instant.now().minusSeconds(3600),
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
            null
        );
        return usuario;
    }
}
