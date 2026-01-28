package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity.UsuarioEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

  private final UsuarioMapper mapper = new UsuarioMapper();

  @Test
  @DisplayName("Debe convertir Usuario de dominio a UsuarioEntity")
  void debeConvertirDominioAEntity() {
    // Arrange
    Usuario usuario =
        Usuario.create(
            Username.of("testuser"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));

    // Act
    UsuarioEntity entity = mapper.toEntity(usuario);

    // Assert
    assertNotNull(entity);
    assertEquals(usuario.getId().value(), entity.getId());
    assertEquals("testuser", entity.getUsername());
    assertEquals("test@test.com", entity.getEmail());
    assertEquals("$2a$10$hash", entity.getPasswordHash());
    assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, entity.getStatus());
    assertEquals(Rol.USER, entity.getRole());
    assertEquals(Idioma.ESP, entity.getLanguage());
    assertTrue(entity.isNotificationsActive());
    assertNotNull(entity.getCreatedAt());
    assertNotNull(entity.getUpdatedAt());
  }

  @Test
  @DisplayName("Debe convertir UsuarioEntity a Usuario de dominio")
  void debeConvertirEntityADominio() {
    // Arrange
    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setAvatar(null);
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setNotificationsActive(true);
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setDiscordUserId(null);
    entity.setDiscordUsername(null);
    entity.setDiscordLinkedAt(null);
    entity.setTokenVerificacion(null);
    entity.setTokenVerificacionExpiracion(null);
    entity.setTokenRestablecimiento(null);
    entity.setTokenRestablecimientoExpiracion(null);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertNotNull(usuario);
    assertEquals(entity.getId(), usuario.getId().value());
    assertEquals("testuser", usuario.getUsername().value());
    assertEquals("test@test.com", usuario.getEmail().value());
    assertEquals("$2a$10$hash", usuario.getPasswordHash().value());
    assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    assertEquals(Rol.USER, usuario.getRole());
    assertEquals(Idioma.ESP, usuario.getLanguage());
    assertTrue(usuario.isNotificationsActive());
  }

  @Test
  @DisplayName("Debe manejar correctamente valores opcionales nulos (Avatar, Discord)")
  void debeManejareValoresOpcionalesNulos() {
    // Arrange
    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setAvatar(null);
    entity.setDiscordUserId(null);
    entity.setDiscordUsername(null);
    entity.setDiscordLinkedAt(null);
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setNotificationsActive(true);
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setTokenVerificacion(null);
    entity.setTokenVerificacionExpiracion(null);
    entity.setTokenRestablecimiento(null);
    entity.setTokenRestablecimientoExpiracion(null);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertTrue(usuario.getAvatar().isEmpty());
    assertTrue(usuario.getDiscordUserId().isEmpty());
    assertTrue(usuario.getDiscordUsername().isEmpty());
    assertNull(usuario.getDiscordLinkedAt());
  }

  @Test
  @DisplayName("Debe manejar correctamente valores opcionales con datos (Avatar, Discord)")
  void debeManejareValoresOpcionalesConDatos() {
    // Arrange
    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setAvatar("https://example.com/avatar.png");
    entity.setDiscordUserId("123456789");
    entity.setDiscordUsername("player#1234");
    entity.setDiscordLinkedAt(Instant.now());
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ENG);
    entity.setNotificationsActive(false);
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setTokenVerificacion(null);
    entity.setTokenVerificacionExpiracion(null);
    entity.setTokenRestablecimiento(null);
    entity.setTokenRestablecimientoExpiracion(null);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertFalse(usuario.getAvatar().isEmpty());
    assertEquals("https://example.com/avatar.png", usuario.getAvatar().url());
    assertFalse(usuario.getDiscordUserId().isEmpty());
    assertEquals("123456789", usuario.getDiscordUserId().value());
    assertFalse(usuario.getDiscordUsername().isEmpty());
    assertEquals("player#1234", usuario.getDiscordUsername().value());
    assertNotNull(usuario.getDiscordLinkedAt());
    assertEquals(Idioma.ENG, usuario.getLanguage());
    assertFalse(usuario.isNotificationsActive());
  }

  @Test
  @DisplayName("Debe convertir correctamente tokens de verificación")
  void debeConvertirTokensDeVerificacion() {
    // Arrange
    String tokenValue = UUID.randomUUID().toString();
    Instant expiracion = Instant.now().plusSeconds(86400);

    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setNotificationsActive(true);
    entity.setStatus(EstadoUsuario.PENDIENTE_DE_VERIFICACION);
    entity.setTokenVerificacion(tokenValue);
    entity.setTokenVerificacionExpiracion(expiracion);
    entity.setTokenRestablecimiento(null);
    entity.setTokenRestablecimientoExpiracion(null);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertNotNull(usuario.getTokenVerificacion());
    assertFalse(usuario.getTokenVerificacion().isEmpty());
    assertEquals(tokenValue, usuario.getTokenVerificacion().value());
    assertEquals(expiracion, usuario.getTokenVerificacionExpiracion());
  }

  @Test
  @DisplayName("Debe convertir correctamente tokens de restablecimiento")
  void debeConvertirTokensDeRestablecimiento() {
    // Arrange
    String tokenValue = UUID.randomUUID().toString();
    Instant expiracion = Instant.now().plusSeconds(86400);

    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("testuser");
    entity.setEmail("test@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setNotificationsActive(true);
    entity.setStatus(EstadoUsuario.ACTIVO);
    entity.setTokenVerificacion(null);
    entity.setTokenVerificacionExpiracion(null);
    entity.setTokenRestablecimiento(tokenValue);
    entity.setTokenRestablecimientoExpiracion(expiracion);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertNotNull(usuario.getTokenRestablecimiento());
    assertFalse(usuario.getTokenRestablecimiento().isEmpty());
    assertEquals(tokenValue, usuario.getTokenRestablecimiento().value());
    assertEquals(expiracion, usuario.getTokenRestablecimientoExpiracion());
  }

  @Test
  @DisplayName("Debe realizar conversión bidireccional correctamente")
  void debeRealizarConversionBidireccional() {
    // Arrange
    Usuario usuarioOriginal =
        Usuario.create(
            Username.of("bidirectional"),
            Email.of("bidirectional@test.com"),
            PasswordHash.of("$2a$10$hash"));

    // Agregar datos opcionales
    usuarioOriginal.changeAvatar(Avatar.of("https://example.com/avatar.png"));
    usuarioOriginal.linkDiscord(DiscordUserId.of("987654321"), DiscordUsername.of("gamer#5678"));

    // Act - Primera conversión: Dominio -> Entity
    UsuarioEntity entity = mapper.toEntity(usuarioOriginal);

    // Act - Segunda conversión: Entity -> Dominio
    Usuario usuarioReconstruido = mapper.toDomain(entity);

    // Assert - Verificar que los datos se preservaron
    assertEquals(usuarioOriginal.getId().value(), usuarioReconstruido.getId().value());
    assertEquals(usuarioOriginal.getUsername().value(), usuarioReconstruido.getUsername().value());
    assertEquals(usuarioOriginal.getEmail().value(), usuarioReconstruido.getEmail().value());
    assertEquals(
        usuarioOriginal.getPasswordHash().value(), usuarioReconstruido.getPasswordHash().value());
    assertEquals(usuarioOriginal.getAvatar().url(), usuarioReconstruido.getAvatar().url());
    assertEquals(
        usuarioOriginal.getDiscordUserId().value(), usuarioReconstruido.getDiscordUserId().value());
    assertEquals(
        usuarioOriginal.getDiscordUsername().value(),
        usuarioReconstruido.getDiscordUsername().value());
    assertEquals(usuarioOriginal.getStatus(), usuarioReconstruido.getStatus());
    assertEquals(usuarioOriginal.getRole(), usuarioReconstruido.getRole());
    assertEquals(usuarioOriginal.getLanguage(), usuarioReconstruido.getLanguage());
  }

  @Test
  @DisplayName("Debe manejar todos los estados de usuario")
  void debeManejarTodosLosEstados() {
    // Arrange & Act & Assert para cada estado
    for (EstadoUsuario estado : EstadoUsuario.values()) {
      UsuarioEntity entity = new UsuarioEntity();
      entity.setId(UUID.randomUUID());
      entity.setUsername("user" + estado.name());
      entity.setEmail("user" + estado.name() + "@test.com");
      entity.setPasswordHash("$2a$10$hash");
      entity.setCreatedAt(Instant.now());
      entity.setUpdatedAt(Instant.now());
      entity.setRole(Rol.USER);
      entity.setLanguage(Idioma.ESP);
      entity.setNotificationsActive(true);
      entity.setStatus(estado);

      Usuario usuario = mapper.toDomain(entity);

      assertEquals(
          estado,
          usuario.getStatus(),
          "Estado " + estado.name() + " no se convirtió correctamente");
    }
  }

  @Test
  @DisplayName("Debe manejar todos los roles de usuario")
  void debeManejarTodosLosRoles() {
    // Arrange & Act & Assert para cada rol
    for (Rol rol : Rol.values()) {
      UsuarioEntity entity = new UsuarioEntity();
      entity.setId(UUID.randomUUID());
      entity.setUsername("user" + rol.name());
      entity.setEmail("user" + rol.name() + "@test.com");
      entity.setPasswordHash("$2a$10$hash");
      entity.setCreatedAt(Instant.now());
      entity.setUpdatedAt(Instant.now());
      entity.setRole(rol);
      entity.setLanguage(Idioma.ESP);
      entity.setNotificationsActive(true);
      entity.setStatus(EstadoUsuario.ACTIVO);

      Usuario usuario = mapper.toDomain(entity);

      assertEquals(rol, usuario.getRole(), "Rol " + rol.name() + " no se convirtió correctamente");
    }
  }

  @Test
  @DisplayName("Debe manejar todos los idiomas")
  void debeManejarTodosLosIdiomas() {
    // Arrange & Act & Assert para cada idioma
    for (Idioma idioma : Idioma.values()) {
      UsuarioEntity entity = new UsuarioEntity();
      entity.setId(UUID.randomUUID());
      entity.setUsername("user" + idioma.name());
      entity.setEmail("user" + idioma.name() + "@test.com");
      entity.setPasswordHash("$2a$10$hash");
      entity.setCreatedAt(Instant.now());
      entity.setUpdatedAt(Instant.now());
      entity.setRole(Rol.USER);
      entity.setLanguage(idioma);
      entity.setNotificationsActive(true);
      entity.setStatus(EstadoUsuario.ACTIVO);

      Usuario usuario = mapper.toDomain(entity);

      assertEquals(
          idioma,
          usuario.getLanguage(),
          "Idioma " + idioma.name() + " no se convirtió correctamente");
    }
  }

  @Test
  @DisplayName("Debe preservar timestamps durante conversión")
  void debePreservarTimestamps() {
    // Arrange
    Instant createdAt = Instant.parse("2024-01-01T10:00:00Z");
    Instant updatedAt = Instant.parse("2024-01-02T15:30:00Z");
    Instant discordLinkedAt = Instant.parse("2024-01-03T12:00:00Z");

    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(UUID.randomUUID());
    entity.setUsername("timestamp_test");
    entity.setEmail("timestamp@test.com");
    entity.setPasswordHash("$2a$10$hash");
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    entity.setDiscordLinkedAt(discordLinkedAt);
    entity.setRole(Rol.USER);
    entity.setLanguage(Idioma.ESP);
    entity.setNotificationsActive(true);
    entity.setStatus(EstadoUsuario.ACTIVO);

    // Act
    Usuario usuario = mapper.toDomain(entity);

    // Assert
    assertEquals(createdAt, usuario.getCreatedAt());
    assertEquals(updatedAt, usuario.getUpdatedAt());
    assertEquals(discordLinkedAt, usuario.getDiscordLinkedAt());
  }
}
