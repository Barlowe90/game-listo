package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioNoEncontradoException;
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
class EditarPerfilUsuarioUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;
    
    @Test
    @DisplayName("Debe editar avatar del usuario")
    void debeEditarAvatarDelUsuario() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            "https://example.com/new-avatar.jpg",
            null,
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertEquals("https://example.com/new-avatar.jpg", resultado.avatar());
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe editar idioma del usuario")
    void debeEditarIdiomaDelUsuario() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            null,
            "ENG",
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertEquals("ENG", resultado.language());
    }
    
    @Test
    @DisplayName("Debe habilitar notificaciones del usuario")
    void debeHabilitarNotificacionesDelUsuario() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            null,
            null,
            true
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        usuario.disableNotifications(); // Deshabilitar primero
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertTrue(resultado.notificationsActive());
    }
    
    @Test
    @DisplayName("Debe deshabilitar notificaciones del usuario")
    void debeDeshabilitarNotificacionesDelUsuario() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            null,
            null,
            false
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertFalse(resultado.notificationsActive());
    }
    
    @Test
    @DisplayName("Debe editar múltiples campos a la vez")
    void debeEditarMultiplesCamposALaVez() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            "https://example.com/avatar.jpg",
            "ENG",
            false
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertEquals("https://example.com/avatar.jpg", resultado.avatar());
        assertEquals("ENG", resultado.language());
        assertFalse(resultado.notificationsActive());
    }
    
    @Test
    @DisplayName("Debe ignorar campos nulos sin modificar el usuario")
    void debeIgnorarCamposNulosSinModificarUsuario() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            null,
            null,
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        String avatarOriginal = usuario.getAvatar().url();
        Idioma idiomaOriginal = usuario.getLanguage();
        boolean notificationsOriginal = usuario.isNotificationsActive();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertEquals(avatarOriginal, resultado.avatar());
        assertEquals(idiomaOriginal.name(), resultado.language());
        assertEquals(notificationsOriginal, resultado.notificationsActive());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            "https://example.com/avatar.jpg",
            null,
            null
        );
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        UsuarioNoEncontradoException exception = assertThrows(
            UsuarioNoEncontradoException.class,
            () -> editarPerfilUsuarioUseCase.execute(command)
        );
        
        assertNotNull(exception);
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ID tiene formato inválido")
    void debeLanzarExcepcionSiIdTieneFormatoInvalido() {
        // Arrange
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            "id-invalido",
            "https://example.com/avatar.jpg",
            null,
            null
        );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> editarPerfilUsuarioUseCase.execute(command)
        );
        
        assertTrue(exception.getMessage().contains("Formato de UUID inválido"));
        verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
    }
    
    @Test
    @DisplayName("Debe validar URL del avatar")
    void debeValidarUrlDelAvatar() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        String urlLarga = "https://example.com/" + "a".repeat(500);
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            urlLarga,
            null,
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> editarPerfilUsuarioUseCase.execute(command)
        );
        
        assertTrue(exception.getMessage().contains("no puede exceder 500 caracteres"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si idioma es inválido")
    void debeLanzarExcepcionSiIdiomaEsInvalido() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            null,
            "IDIOMA_INVALIDO",
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> editarPerfilUsuarioUseCase.execute(command)
        );
    }
    
    @Test
    @DisplayName("Debe actualizar timestamp al editar perfil")
    void debeActualizarTimestampAlEditarPerfil() throws InterruptedException {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(
            usuarioIdString,
            "https://example.com/avatar.jpg",
            null,
            null
        );
        
        Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        editarPerfilUsuarioUseCase.execute(command);
        
        // Assert
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    // Helper method
    private Usuario crearUsuarioDefault(UsuarioId id) {
        return Usuario.reconstitute(
            id,
            Username.of("testuser"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash"),
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
            false,
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null
        );
    }
}
