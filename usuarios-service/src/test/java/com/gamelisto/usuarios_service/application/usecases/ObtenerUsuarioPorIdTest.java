package com.gamelisto.usuarios_service.application.usecases;

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
class ObtenerUsuarioPorIdTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private ObtenerUsuarioPorId obtenerUsuarioPorId;
    
    @Test
    @DisplayName("Debe obtener usuario por ID exitosamente")
    void debeObtenerUsuarioPorIdExitosamente() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
        
        Usuario usuario = Usuario.reconstitute(
            usuarioId,
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
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act
        UsuarioDTO resultado = obtenerUsuarioPorId.execute(usuarioIdString);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioIdString, resultado.id());
        assertEquals("testuser", resultado.username());
        assertEquals("test@test.com", resultado.email());
        assertEquals("ACTIVO", resultado.status());
        
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        UsuarioNoEncontradoException exception = assertThrows(
            UsuarioNoEncontradoException.class,
            () -> obtenerUsuarioPorId.execute(usuarioIdString)
        );
        
        assertNotNull(exception);
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ID tiene formato inválido")
    void debeLanzarExcepcionSiIdTieneFormatoInvalido() {
        // Arrange
        String idInvalido = "no-es-uuid";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> obtenerUsuarioPorId.execute(idInvalido)
        );
        
        assertTrue(exception.getMessage().contains("Formato de UUID inválido"));
        verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
    }
    
    @Test
    @DisplayName("Debe retornar DTO con todos los campos del usuario")
    void debeRetornarDTOConTodosLosCampos() {
        // Arrange
        String usuarioIdString = UUID.randomUUID().toString();
        UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
        Instant ahora = Instant.now();
        
        Usuario usuario = Usuario.reconstitute(
            usuarioId,
            Username.of("jugador"),
            Email.of("jugador@test.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.of("https://example.com/avatar.jpg"),
            ahora,
            ahora,
            Rol.ADMIN,
            Idioma.ENG,
            false,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.of("123456"),
            DiscordUsername.of("player#1234"),
            ahora,
            true,
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null
        );
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act
        UsuarioDTO resultado = obtenerUsuarioPorId.execute(usuarioIdString);
        
        // Assert
        assertEquals(usuarioIdString, resultado.id());
        assertEquals("jugador", resultado.username());
        assertEquals("jugador@test.com", resultado.email());
        assertEquals("https://example.com/avatar.jpg", resultado.avatar());
        assertEquals("ADMIN", resultado.role());
        assertEquals("ENG", resultado.language());
        assertFalse(resultado.notificationsActive());
        assertEquals("SUSPENDIDO", resultado.status());
        assertEquals("123456", resultado.discordUserId());
        assertEquals("player#1234", resultado.discordUsername());
        assertTrue(resultado.discordConsent());
        assertNotNull(resultado.discordLinkedAt());
    }
    
    @Test
    @DisplayName("Debe convertir correctamente ID de string a UsuarioId")
    void debeConvertirCorrectamenteIdDeStringAUsuarioId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String usuarioIdString = uuid.toString();
        UsuarioId expectedId = UsuarioId.fromString(usuarioIdString);
        
        Usuario usuario = crearUsuarioDefault(expectedId);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act
        obtenerUsuarioPorId.execute(usuarioIdString);
        
        // Assert
        verify(repositorioUsuarios).findById(argThat(id -> 
            id.value().equals(uuid)
        ));
    }
    
    @Test
    @DisplayName("Debe manejar UUID con mayúsculas y minúsculas")
    void debeManejareUUIDConMayusculasYMinusculas() {
        // Arrange
        String usuarioIdUpper = "550E8400-E29B-41D4-A716-446655440000";
        UsuarioId usuarioId = UsuarioId.fromString(usuarioIdUpper);
        Usuario usuario = crearUsuarioDefault(usuarioId);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        
        // Act
        UsuarioDTO resultado = obtenerUsuarioPorId.execute(usuarioIdUpper);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioIdUpper.toLowerCase(), resultado.id());
    }
    
    // Helper method
    private Usuario crearUsuarioDefault(UsuarioId id) {
        return Usuario.reconstitute(
            id,
            Username.of("test"),
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
