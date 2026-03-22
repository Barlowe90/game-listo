package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.usuarios.EditarPerfilUsuarioUseCase;
import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Rol;
import com.gamelisto.usuarios.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import com.gamelisto.usuarios.domain.usuario.Username;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarPerfilUsuarioUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;
  @Mock private IUsuarioPublisher usuarioPublisher;

  @InjectMocks private EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;

  @Test
  @DisplayName("Debe editar multiples campos a la vez")
  void debeEditarMultiplesCamposALaVez() {
    UUID usuarioId = UUID.randomUUID();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioId, "https://example.com/avatar.jpg", "ENG");

    Usuario usuario = crearUsuarioDefault(UsuarioId.of(usuarioId));
    usuario.linkDiscord(DiscordUserId.of("123456789"));

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = editarPerfilUsuarioUseCase.execute(command);

    assertEquals("https://example.com/avatar.jpg", resultado.avatar());
    assertEquals("ENG", resultado.language());

    ArgumentCaptor<UsuarioActualizado> eventCaptor =
        ArgumentCaptor.forClass(UsuarioActualizado.class);
    verify(usuarioPublisher).publicarUsuarioActualizado(eventCaptor.capture());
    assertEquals(usuarioId.toString(), eventCaptor.getValue().usuarioId());
    assertEquals("testuser", eventCaptor.getValue().username());
    assertEquals("https://example.com/avatar.jpg", eventCaptor.getValue().avatar());
    assertEquals("123456789", eventCaptor.getValue().discordUserId());
  }

  @Test
  @DisplayName("Debe ignorar campos nulos sin modificar el usuario")
  void debeIgnorarCamposNulosSinModificarUsuario() {
    UUID usuarioId = UUID.randomUUID();
    EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(usuarioId, null, null);

    Usuario usuario = crearUsuarioDefault(UsuarioId.of(usuarioId));
    String avatarOriginal = usuario.getAvatar().url();
    Idioma idiomaOriginal = usuario.getLanguage();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = editarPerfilUsuarioUseCase.execute(command);

    assertEquals(avatarOriginal, resultado.avatar());
    assertEquals(idiomaOriginal.name(), resultado.language());
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    UUID usuarioId = UUID.randomUUID();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioId, "https://example.com/avatar.jpg", null);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> editarPerfilUsuarioUseCase.execute(command));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si el ID tiene formato invalido")
  void debeLanzarExcepcionSiIdTieneFormatoInvalido() {
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand((UUID) null, "https://example.com/avatar.jpg", null);

    assertThrows(DomainException.class, () -> editarPerfilUsuarioUseCase.execute(command));
    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe validar longitud del avatar")
  void debeValidarUrlDelAvatar() {
    UUID usuarioId = UUID.randomUUID();
    String urlLarga = "https://example.com/" + "a".repeat(500);
    EditarPerfilUsuarioCommand command = new EditarPerfilUsuarioCommand(usuarioId, urlLarga, null);

    Usuario usuario = crearUsuarioDefault(UsuarioId.of(usuarioId));
    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    assertThrows(DomainException.class, () -> editarPerfilUsuarioUseCase.execute(command));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si el idioma es invalido")
  void debeLanzarExcepcionSiIdiomaEsInvalido() {
    UUID usuarioId = UUID.randomUUID();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioId, null, "IDIOMA_INVALIDO");

    Usuario usuario = crearUsuarioDefault(UsuarioId.of(usuarioId));
    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    assertThrows(ApplicationException.class, () -> editarPerfilUsuarioUseCase.execute(command));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  private Usuario crearUsuarioDefault(UsuarioId id) {
    return Usuario.reconstitute(
        id,
        Username.of("testuser"),
        Email.of("test@test.com"),
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
}
