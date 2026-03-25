package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.usecases.admin.ObtenerTodosLosUsuariosUseCase;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerTodosLosUsuariosUseCase - Tests")
class ObtenerTodosLosUsuariosUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @InjectMocks private ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe retornar lista vacía si no hay usuarios")
  void debeRetornarListaVaciaSiNoHayUsuarios() {
    // Arrange
    when(repositorioUsuarios.findAll()).thenReturn(Collections.emptyList());

    // Act
    List<UsuarioResult> resultado = obtenerTodosLosUsuariosUseCase.execute();

    // Assert
    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(repositorioUsuarios).findAll();
  }

  @Test
  @DisplayName("Debe retornar un usuario cuando existe uno")
  void debeRetornarUnUsuarioCuandoExisteUno() {
    // Arrange
    Usuario usuario = crearUsuario("testuser", "test@test.com");
    when(repositorioUsuarios.findAll()).thenReturn(List.of(usuario));

    // Act
    List<UsuarioResult> resultado = obtenerTodosLosUsuariosUseCase.execute();

    // Assert
    assertNotNull(resultado);
    assertEquals(1, resultado.size());
    assertEquals("testuser", resultado.get(0).username());
    assertEquals("test@test.com", resultado.get(0).email());
  }

  @Test
  @DisplayName("Debe retornar múltiples usuarios")
  void debeRetornarMultiplesUsuarios() {
    // Arrange
    Usuario usuario1 = crearUsuario("user1", "user1@test.com");
    Usuario usuario2 = crearUsuario("user2", "user2@test.com");
    Usuario usuario3 = crearUsuario("user3", "user3@test.com");

    when(repositorioUsuarios.findAll()).thenReturn(List.of(usuario1, usuario2, usuario3));

    // Act
    List<UsuarioResult> resultado = obtenerTodosLosUsuariosUseCase.execute();

    // Assert
    assertNotNull(resultado);
    assertEquals(3, resultado.size());

    assertTrue(resultado.stream().anyMatch(u -> u.username().equals("user1")));
    assertTrue(resultado.stream().anyMatch(u -> u.username().equals("user2")));
    assertTrue(resultado.stream().anyMatch(u -> u.username().equals("user3")));
  }

  @Test
  @DisplayName("Debe convertir todos los usuarios a DTO correctamente")
  void debeConvertirTodosLosUsuariosADTOCorrectamente() {
    // Arrange
    Usuario usuario = crearUsuarioCompleto();
    when(repositorioUsuarios.findAll()).thenReturn(List.of(usuario));

    // Act
    List<UsuarioResult> resultado = obtenerTodosLosUsuariosUseCase.execute();

    // Assert
    assertNotNull(resultado);
    assertEquals(1, resultado.size());

    UsuarioResult dto = resultado.get(0);
    assertEquals("completeuser", dto.username());
    assertEquals("complete@test.com", dto.email());
    assertEquals("ACTIVO", dto.status());
    assertEquals("USER", dto.role());
    assertEquals("ESP", dto.language());
  }

  @Test
  @DisplayName("Debe llamar a findAll del repositorio")
  void debeLlamarAFindAllDelRepositorio() {
    // Arrange
    when(repositorioUsuarios.findAll()).thenReturn(Collections.emptyList());

    // Act
    obtenerTodosLosUsuariosUseCase.execute();

    // Assert
    verify(repositorioUsuarios, times(1)).findAll();
  }

  // ========== MÉTODOS AUXILIARES ==========

  private Usuario crearUsuario(String username, String email) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of(username),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Rol.USER,
        Idioma.ESP,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }

  private Usuario crearUsuarioCompleto() {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("completeuser"),
        Email.of("complete@test.com"),
        PasswordHash.of("$2a$10$hash"),
        Avatar.of("https://example.com/avatar.jpg"),
        Rol.USER,
        Idioma.ESP,
        EstadoUsuario.ACTIVO,
        DiscordUserId.of("123456789"),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }
}
