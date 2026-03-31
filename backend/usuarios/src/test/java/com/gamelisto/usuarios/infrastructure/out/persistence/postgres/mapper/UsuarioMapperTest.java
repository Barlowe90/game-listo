package com.gamelisto.usuarios.infrastructure.out.persistence.postgres.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Rol;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.Username;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.UsuarioEntity;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.UsuarioMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

  private final UsuarioMapper mapper = new UsuarioMapper();

  @Test
  @DisplayName("Debe convertir Usuario de dominio a entidad")
  void debeConvertirDominioAEntity() {
    Usuario usuario =
        Usuario.create(
            Username.of("testuser"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));

    UsuarioEntity entity = mapper.toEntity(usuario);

    assertNotNull(entity);
    assertEquals(usuario.getId().value(), entity.getId());
    assertEquals("testuser", entity.getUsername());
    assertEquals("test@test.com", entity.getEmail());
    assertEquals("$2a$10$hash", entity.getPasswordHash());
    assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, entity.getStatus());
    assertEquals(Rol.USER, entity.getRole());
    assertEquals(Idioma.ESP, entity.getLanguage());
  }

  @Test
  @DisplayName("Debe convertir UsuarioEntity a dominio")
  void debeConvertirEntityADominio() {
    UsuarioEntity entity = entidadBase();
    entity.setStatus(EstadoUsuario.ACTIVO);

    Usuario usuario = mapper.toDomain(entity);

    assertNotNull(usuario);
    assertEquals(entity.getId(), usuario.getId().value());
    assertEquals("testuser", usuario.getUsername().value());
    assertEquals("test@test.com", usuario.getEmail().value());
    assertEquals("$2a$10$hash", usuario.getPasswordHash().value());
    assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    assertEquals(Rol.USER, usuario.getRole());
    assertEquals(Idioma.ESP, usuario.getLanguage());
  }

  @Test
  @DisplayName("Debe manejar valores opcionales nulos")
  void debeManejarValoresOpcionalesNulos() {
    UsuarioEntity entity = entidadBase();
    entity.setAvatar(null);
    entity.setDiscordUserId(null);

    Usuario usuario = mapper.toDomain(entity);

    assertTrue(usuario.getAvatar().isEmpty());
    assertTrue(usuario.getDiscordUserId().isEmpty());
  }

  @Test
  @DisplayName("Debe manejar valores opcionales con datos")
  void debeManejarValoresOpcionalesConDatos() {
    UsuarioEntity entity = entidadBase();
    entity.setAvatar("https://example.com/avatar.png");
    entity.setDiscordUserId("123456789");
    entity.setLanguage(Idioma.ENG);
    entity.setStatus(EstadoUsuario.ACTIVO);

    Usuario usuario = mapper.toDomain(entity);

    assertEquals("https://example.com/avatar.png", usuario.getAvatar().url());
    assertEquals("123456789", usuario.getDiscordUserId().value());
    assertEquals(Idioma.ENG, usuario.getLanguage());
  }

  @Test
  @DisplayName("Debe convertir correctamente tokens de verificacion")
  void debeConvertirTokensDeVerificacion() {
    String tokenValue = UUID.randomUUID().toString();
    Instant expiracion = Instant.now().plusSeconds(86400);

    UsuarioEntity entity = entidadBase();
    entity.setStatus(EstadoUsuario.PENDIENTE_DE_VERIFICACION);
    entity.setTokenVerificacion(tokenValue);
    entity.setTokenVerificacionExpiracion(expiracion);

    Usuario usuario = mapper.toDomain(entity);

    assertEquals(tokenValue, usuario.getTokenVerificacion().value());
    assertEquals(expiracion, usuario.getTokenVerificacionExpiracion());
  }

  @Test
  @DisplayName("Debe convertir correctamente tokens de restablecimiento")
  void debeConvertirTokensDeRestablecimiento() {
    String tokenValue = UUID.randomUUID().toString();
    Instant expiracion = Instant.now().plusSeconds(86400);

    UsuarioEntity entity = entidadBase();
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setTokenRestablecimiento(tokenValue);
    entity.setTokenRestablecimientoExpiracion(expiracion);

    Usuario usuario = mapper.toDomain(entity);

    assertEquals(tokenValue, usuario.getTokenRestablecimiento().value());
    assertEquals(expiracion, usuario.getTokenRestablecimientoExpiracion());
  }

  @Test
  @DisplayName("Debe preservar datos en conversion bidireccional")
  void debeRealizarConversionBidireccional() {
    Usuario usuarioOriginal =
        Usuario.create(
            Username.of("bidirectional"),
            Email.of("bidirectional@test.com"),
            PasswordHash.of("$2a$10$hash"));
    usuarioOriginal.changeAvatar(Avatar.of("https://example.com/avatar.png"));
    usuarioOriginal.linkDiscord(DiscordUserId.of("987654321"));

    UsuarioEntity entity = mapper.toEntity(usuarioOriginal);
    Usuario usuarioReconstruido = mapper.toDomain(entity);

    assertEquals(usuarioOriginal.getId().value(), usuarioReconstruido.getId().value());
    assertEquals(usuarioOriginal.getUsername().value(), usuarioReconstruido.getUsername().value());
    assertEquals(usuarioOriginal.getEmail().value(), usuarioReconstruido.getEmail().value());
    assertEquals(
        usuarioOriginal.getPasswordHash().value(), usuarioReconstruido.getPasswordHash().value());
    assertEquals(usuarioOriginal.getAvatar().url(), usuarioReconstruido.getAvatar().url());
    assertEquals(
        usuarioOriginal.getDiscordUserId().value(), usuarioReconstruido.getDiscordUserId().value());
  }

  private UsuarioEntity entidadBase() {
    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setTokenVerificacion(null);
    entity.setTokenVerificacionExpiracion(null);
    entity.setTokenRestablecimiento(null);
    entity.setTokenRestablecimientoExpiracion(null);
    return entity;
  }
}
