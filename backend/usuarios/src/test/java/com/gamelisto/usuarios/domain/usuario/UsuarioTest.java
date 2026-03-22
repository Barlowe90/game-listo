package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioTest {

  @Test
  @DisplayName("Debe crear nuevo usuario con valores por defecto")
  void debeCrearNuevoUsuarioConCreate() {
    Usuario usuario =
        Usuario.create(
            Username.of("jugador123"),
            Email.of("jugador@test.com"),
            PasswordHash.of("$2a$10$hashed"));

    assertNotNull(usuario.getId());
    assertEquals("jugador123", usuario.getUsername().value());
    assertEquals("jugador@test.com", usuario.getEmail().value());
    assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
    assertEquals(Rol.USER, usuario.getRole());
    assertEquals(Idioma.ESP, usuario.getLanguage());
    assertTrue(usuario.getAvatar().isEmpty());
    assertTrue(usuario.getDiscordUserId().isEmpty());
  }

  @Test
  @DisplayName("Debe reconstituir usuario desde persistencia")
  void debeReconstituirUsuarioConReconstitute() {
    UsuarioId id = UsuarioId.generate();

    Usuario usuario =
        Usuario.reconstitute(
            id,
            Username.of("testuser"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.of("https://example.com/avatar.jpg"),
            Rol.ADMIN,
            Idioma.ENG,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("123456"),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    assertEquals(id.value(), usuario.getId().value());
    assertEquals("testuser", usuario.getUsername().value());
    assertEquals("test@test.com", usuario.getEmail().value());
    assertEquals("https://example.com/avatar.jpg", usuario.getAvatar().url());
    assertEquals(Rol.ADMIN, usuario.getRole());
    assertEquals(Idioma.ENG, usuario.getLanguage());
    assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    assertEquals("123456", usuario.getDiscordUserId().value());
  }

  @Test
  @DisplayName("Debe validar argumentos obligatorios al crear")
  void debeValidarArgumentosObligatorios() {
    Email email = Email.of("test@test.com");
    PasswordHash passwordHash = PasswordHash.of("$2a$10$hash");

    assertThrows(DomainException.class, () -> Usuario.create(null, email, passwordHash));
    assertThrows(
        DomainException.class,
        () -> Usuario.create(Username.of("testuser"), null, passwordHash));
    assertThrows(
        DomainException.class,
        () -> Usuario.create(Username.of("testuser"), email, null));
  }

  @Test
  @DisplayName("Debe cambiar email")
  void debeCambiarEmail() {
    Usuario usuario = crearUsuarioDefault();

    usuario.changeEmail(Email.of("nuevo@test.com"));

    assertEquals("nuevo@test.com", usuario.getEmail().value());
  }

  @Test
  @DisplayName("Debe cambiar password hash")
  void debeCambiarPasswordHash() {
    Usuario usuario = crearUsuarioDefault();

    usuario.changePasswordHash(PasswordHash.of("$2a$10$newHash"));

    assertEquals("$2a$10$newHash", usuario.getPasswordHash().value());
  }

  @Test
  @DisplayName("Debe lanzar excepcion al cambiar password hash a nulo")
  void debeLanzarExcepcionAlCambiarPasswordHashANulo() {
    Usuario usuario = crearUsuarioDefault();

    assertThrows(DomainException.class, () -> usuario.changePasswordHash(null));
  }

  @Test
  @DisplayName("Debe cambiar avatar y aceptar nulo como vacio")
  void debeCambiarAvatarYPermitirNulo() {
    Usuario usuario = crearUsuarioDefault();

    usuario.changeAvatar(Avatar.of("https://example.com/new-avatar.jpg"));
    assertEquals("https://example.com/new-avatar.jpg", usuario.getAvatar().url());

    usuario.changeAvatar(null);
    assertTrue(usuario.getAvatar().isEmpty());
  }

  @Test
  @DisplayName("Debe cambiar idioma y mantener ESP cuando es nulo")
  void debeCambiarIdioma() {
    Usuario usuario = crearUsuarioDefault();

    usuario.changeLanguage(Idioma.ENG);
    assertEquals(Idioma.ENG, usuario.getLanguage());

    usuario.changeLanguage(null);
    assertEquals(Idioma.ESP, usuario.getLanguage());
  }

  @Test
  @DisplayName("Debe gestionar estados de usuario")
  void debeGestionarEstados() {
    Usuario usuario = crearUsuarioDefault();

    usuario.suspend();
    assertTrue(usuario.isSuspended());
    assertFalse(usuario.isActive());

    usuario.activate();
    assertTrue(usuario.isActive());

    usuario.delete();
    assertTrue(usuario.isDeleted());
    assertThrows(DomainException.class, usuario::activate);
  }

  @Test
  @DisplayName("Debe activar usuario pendiente de verificacion")
  void debeActivarUsuarioPendienteDeVerificacion() {
    Usuario usuario =
        Usuario.create(
            Username.of("test"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));

    usuario.activate();

    assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
  }

  @Test
  @DisplayName("Debe vincular cuenta de Discord con user ID")
  void debeVincularCuentaDeDiscord() {
    Usuario usuario = crearUsuarioDefault();
    DiscordUserId discordId = DiscordUserId.of("123456789");
    Awaitility.await().pollDelay(Duration.ofMillis(10)).until(() -> true);

    usuario.linkDiscord(discordId);

    assertEquals("123456789", usuario.getDiscordUserId().value());
    assertTrue(usuario.hasDiscordLinked());
  }

  @Test
  @DisplayName("Debe lanzar excepcion al vincular Discord con ID nulo o vacio")
  void debeValidarIdDeDiscordAlVincular() {
    Usuario usuario = crearUsuarioDefault();

    assertThrows(DomainException.class, () -> usuario.linkDiscord(null));
    assertThrows(DomainException.class, () -> usuario.linkDiscord(DiscordUserId.empty()));
  }

  @Test
  @DisplayName("Debe desvincular cuenta de Discord")
  void debeDesvincularCuentaDeDiscord() {
    Usuario usuario = crearUsuarioDefault();
    usuario.linkDiscord(DiscordUserId.of("123456789"));
    Awaitility.await().pollDelay(Duration.ofMillis(10)).until(() -> true);

    usuario.unlinkDiscord();

    assertTrue(usuario.getDiscordUserId().isEmpty());
    assertFalse(usuario.hasDiscordLinked());
  }

  @Test
  @DisplayName("hasDiscordLinked debe reflejar la vinculacion")
  void hasDiscordLinkedDebeReflejarLaVinculacion() {
    Usuario usuario = crearUsuarioDefault();
    assertFalse(usuario.hasDiscordLinked());

    usuario.linkDiscord(DiscordUserId.of("123456"));

    assertTrue(usuario.hasDiscordLinked());
  }

  @Test
  @DisplayName("toString debe incluir informacion basica del usuario")
  void toStringDebeIncluirInformacionBasica() {
    Usuario usuario = crearUsuarioDefault();

    String resultado = usuario.toString();

    assertTrue(resultado.contains("Usuario"));
    assertTrue(resultado.contains(usuario.getId().toString()));
    assertTrue(resultado.contains(usuario.getUsername().value()));
    assertTrue(resultado.contains(usuario.getEmail().value()));
    assertTrue(resultado.contains(usuario.getStatus().toString()));
  }

  private Usuario crearUsuarioDefault() {
    Usuario usuario =
        Usuario.create(
            Username.of("testuser"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));
    usuario.activate();
    return usuario;
  }
}
