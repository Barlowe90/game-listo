package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.exceptions.TokenVerificacionInvalidoException;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioYaVerificadoException;
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
@DisplayName("VerificarEmailUseCase - Tests")
class VerificarEmailUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private VerificarEmailUseCase verificarEmailUseCase;
    
    // ========== CASOS DE ÉXITO ==========
    
    @Test
    @DisplayName("Debe verificar email exitosamente con token válido")
    void debeVerificarEmailExitosamente() {
        // Arrange
        TokenVerificacion token = TokenVerificacion.generate();
        Instant expiracion = Instant.now().plusSeconds(3600); // 1 hora en el futuro
        
        Usuario usuario = Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of("test@test.com"),
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
            false,
            token,
            expiracion,
            TokenVerificacion.empty(),
            null
        );
        
        when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        VerificarEmailCommand command = new VerificarEmailCommand(token.value());
        
        // Act
        verificarEmailUseCase.execute(command);
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        assertTrue(usuario.getTokenVerificacion().isEmpty());
        assertNull(usuario.getTokenVerificacionExpiracion());
        
        verify(repositorioUsuarios).findByTokenVerificacion(any(TokenVerificacion.class));
        verify(repositorioUsuarios).save(usuario);
    }
    
    @Test
    @DisplayName("Debe cambiar estado de PENDIENTE_DE_VERIFICACION a ACTIVO")
    void debeCambiarEstadoAlVerificar() {
        // Arrange
        TokenVerificacion token = TokenVerificacion.generate();
        Instant expiracion = Instant.now().plusSeconds(3600);
        
        Usuario usuario = crearUsuarioPendiente(token, expiracion);
        
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
        
        when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
            .thenReturn(Optional.of(usuario));
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        VerificarEmailCommand command = new VerificarEmailCommand(token.value());
        
        // Act
        verificarEmailUseCase.execute(command);
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    }
    
    // ========== CASOS DE ERROR ==========
    
    @Test
    @DisplayName("Debe lanzar excepción si token no existe")
    void debeLanzarExcepcionSiTokenNoExiste() {
        // Arrange
        String tokenInvalido = "token_inexistente_123";
        
        when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
            .thenReturn(Optional.empty());
        
        VerificarEmailCommand command = new VerificarEmailCommand(tokenInvalido);
        
        // Act & Assert
        TokenVerificacionInvalidoException exception = assertThrows(
            TokenVerificacionInvalidoException.class,
            () -> verificarEmailUseCase.execute(command)
        );
        
        assertEquals(tokenInvalido, exception.getToken());
        verify(repositorioUsuarios, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si token está expirado")
    void debeLanzarExcepcionSiTokenExpirado() {
        // Arrange
        TokenVerificacion token = TokenVerificacion.generate();
        Instant expiracion = Instant.now().minusSeconds(3600); // 1 hora en el pasado
        
        Usuario usuario = crearUsuarioPendiente(token, expiracion);
        
        when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
            .thenReturn(Optional.of(usuario));
        
        VerificarEmailCommand command = new VerificarEmailCommand(token.value());
        
        // Act & Assert
        TokenVerificacionInvalidoException exception = assertThrows(
            TokenVerificacionInvalidoException.class,
            () -> verificarEmailUseCase.execute(command)
        );
        
        assertNotNull(exception);
        verify(repositorioUsuarios, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si usuario ya está verificado")
    void debeLanzarExcepcionSiUsuarioYaVerificado() {
        // Arrange
        TokenVerificacion token = TokenVerificacion.generate();
        Instant expiracion = Instant.now().plusSeconds(3600);
        
        Usuario usuario = Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Instant.now(),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.ACTIVO, // Ya está activo
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            false,
            token,
            expiracion,
            TokenVerificacion.empty(),
            null
        );
        
        when(repositorioUsuarios.findByTokenVerificacion(any(TokenVerificacion.class)))
            .thenReturn(Optional.of(usuario));
        
        VerificarEmailCommand command = new VerificarEmailCommand(token.value());
        
        // Act & Assert
        UsuarioYaVerificadoException exception = assertThrows(
            UsuarioYaVerificadoException.class,
            () -> verificarEmailUseCase.execute(command)
        );
        
        assertEquals("test@test.com", exception.getEmail());
        verify(repositorioUsuarios, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si token es nulo o vacío")
    void debeLanzarExcepcionSiTokenEsNuloOVacio() {
        // Arrange
        VerificarEmailCommand commandNulo = new VerificarEmailCommand(null);
        VerificarEmailCommand commandVacio = new VerificarEmailCommand("");
        VerificarEmailCommand commandBlanco = new VerificarEmailCommand("   ");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> verificarEmailUseCase.execute(commandNulo));
        assertThrows(IllegalArgumentException.class, () -> verificarEmailUseCase.execute(commandVacio));
        assertThrows(IllegalArgumentException.class, () -> verificarEmailUseCase.execute(commandBlanco));
        
        verify(repositorioUsuarios, never()).findByTokenVerificacion(any());
    }
    
    // ========== HELPER METHODS ==========
    
    private Usuario crearUsuarioPendiente(TokenVerificacion token, Instant expiracion) {
        return Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of("test@test.com"),
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
            false,
            token,
            expiracion,
            TokenVerificacion.empty(),
            null
        );
    }
}
