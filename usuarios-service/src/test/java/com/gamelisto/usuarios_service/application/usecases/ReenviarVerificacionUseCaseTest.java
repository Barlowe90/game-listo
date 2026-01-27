package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.ReenviarVerificacionCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioYaVerificadoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReenviarVerificacionUseCase - Tests")
class ReenviarVerificacionUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private ReenviarVerificacionUseCase reenviarVerificacionUseCase;
    
    // ========== CASOS DE ÉXITO ==========
    
    @Test
    @DisplayName("Debe reenviar verificación exitosamente para usuario pendiente")
    void debeReenviarVerificacionExitosamente() {
        // Arrange
        String email = "pendiente@test.com";
        Usuario usuario = crearUsuarioPendiente(email);
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(email);
        
        // Act
        reenviarVerificacionUseCase.execute(command);
        
        // Assert
        verify(repositorioUsuarios).findByEmail(any(Email.class));
        verify(repositorioUsuarios).save(usuario);
        
        // El token debe haber cambiado
        assertNotNull(usuario.getTokenVerificacion());
        assertFalse(usuario.getTokenVerificacion().isEmpty());
    }
    
    @Test
    @DisplayName("Debe regenerar token al reenviar verificación")
    void debeRegenerarTokenAlReenviarVerificacion() {
        // Arrange
        String email = "pendiente@test.com";
        Usuario usuario = crearUsuarioPendiente(email);
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(email);
        
        // Act
        reenviarVerificacionUseCase.execute(command);
        
        // Assert
        assertNotNull(usuario.getTokenVerificacion());
        assertFalse(usuario.getTokenVerificacion().isEmpty());
        assertNotNull(usuario.getTokenVerificacionExpiracion());
    }
    
    @Test
    @DisplayName("Debe buscar usuario por email")
    void debeBuscarUsuarioPorEmail() {
        // Arrange
        String email = "buscar@test.com";
        Usuario usuario = crearUsuarioPendiente(email);
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(email);
        
        // Act
        reenviarVerificacionUseCase.execute(command);
        
        // Assert
        verify(repositorioUsuarios).findByEmail(argThat(e -> 
            e.value().equals(email)
        ));
    }
    
    // ========== CASOS DE ERROR ==========
    
    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String emailInexistente = "noexiste@test.com";
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.empty());
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(emailInexistente);
        
        // Act & Assert
        assertThrows(
            UsuarioNoEncontradoException.class,
            () -> reenviarVerificacionUseCase.execute(command)
        );
        
        verify(repositorioUsuarios, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el usuario ya está verificado (ACTIVO)")
    void debeLanzarExcepcionSiUsuarioYaEstaActivo() {
        // Arrange
        String email = "activo@test.com";
        Usuario usuario = crearUsuarioActivo(email);
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(email);
        
        // Act & Assert
        UsuarioYaVerificadoException exception = assertThrows(
            UsuarioYaVerificadoException.class,
            () -> reenviarVerificacionUseCase.execute(command)
        );
        
        assertEquals(email, exception.getEmail());
        verify(repositorioUsuarios, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el usuario está suspendido")
    void debeLanzarExcepcionSiUsuarioEstaSuspendido() {
        // Arrange
        String email = "suspendido@test.com";
        Usuario usuario = crearUsuarioSuspendido(email);
        
        when(repositorioUsuarios.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(usuario));
        
        ReenviarVerificacionCommand command = new ReenviarVerificacionCommand(email);
        
        // Act & Assert
        assertThrows(
            UsuarioYaVerificadoException.class,
            () -> reenviarVerificacionUseCase.execute(command)
        );
        
        verify(repositorioUsuarios, never()).save(any());
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private Usuario crearUsuarioPendiente(String email) {
        Usuario usuario = Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of(email),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Instant.now(),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.PENDIENTE_DE_VERIFICACION,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.generate(),
            Instant.now().plusSeconds(3600),
            TokenVerificacion.empty(),
            null
        );
        return usuario;
    }
    
    private Usuario crearUsuarioActivo(String email) {
        return Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of(email),
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
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null
        );
    }
    
    private Usuario crearUsuarioSuspendido(String email) {
        return Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of(email),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Instant.now(),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null
        );
    }
}
