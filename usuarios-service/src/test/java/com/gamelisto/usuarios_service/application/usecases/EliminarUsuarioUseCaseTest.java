package com.gamelisto.usuarios_service.application.usecases;

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
@DisplayName("EliminarUsuarioUseCase - Tests")
class EliminarUsuarioUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @InjectMocks
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;
    
    // ========== CASOS DE ÉXITO ==========
    
    @Test
    @DisplayName("Debe eliminar usuario exitosamente")
    void debeEliminarUsuarioExitosamente() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        Usuario usuario = crearUsuario(usuarioId);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        doNothing().when(repositorioUsuarios).delete(any(Usuario.class));
        
        // Act
        eliminarUsuarioUseCase.execute(usuarioId);
        
        // Assert
        verify(repositorioUsuarios).findById(any(UsuarioId.class));
        verify(repositorioUsuarios).delete(usuario);
    }
    
    @Test
    @DisplayName("Debe buscar usuario por ID antes de eliminar")
    void debeBuscarUsuarioPorIdAntesDeEliminar() {
        // Arrange
        String usuarioId = UUID.randomUUID().toString();
        Usuario usuario = crearUsuario(usuarioId);
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.of(usuario));
        doNothing().when(repositorioUsuarios).delete(any(Usuario.class));
        
        // Act
        eliminarUsuarioUseCase.execute(usuarioId);
        
        // Assert
        verify(repositorioUsuarios).findById(argThat(id -> 
            id.value().toString().equals(usuarioId)
        ));
    }
    
    // ========== CASOS DE ERROR ==========
    
    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdInexistente = UUID.randomUUID().toString();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        UsuarioNoEncontradoException exception = assertThrows(
            UsuarioNoEncontradoException.class,
            () -> eliminarUsuarioUseCase.execute(usuarioIdInexistente)
        );
        
        assertEquals(usuarioIdInexistente, exception.getUsuarioId());
        verify(repositorioUsuarios, never()).delete(any());
    }
    
    @Test
    @DisplayName("No debe llamar a delete si el usuario no existe")
    void noDebeEliminarSiUsuarioNoExiste() {
        // Arrange
        String usuarioIdInexistente = UUID.randomUUID().toString();
        
        when(repositorioUsuarios.findById(any(UsuarioId.class)))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
            UsuarioNoEncontradoException.class,
            () -> eliminarUsuarioUseCase.execute(usuarioIdInexistente)
        );
        
        verify(repositorioUsuarios, never()).delete(any(Usuario.class));
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private Usuario crearUsuario(String id) {
        return Usuario.reconstitute(
            UsuarioId.fromString(id),
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
            null
        );
    }
}
