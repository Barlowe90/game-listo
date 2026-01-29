package com.gamelisto.usuarios_service.domain.usuario;

import java.time.Instant;

public class Usuario {

  private static final int PASSWORD_RESET_TOKEN_TTL_SECONDS = 60 * 60; // 1 hora
  private static final int EMAIL_VERIFICATION_TOKEN_TTL_SECONDS = 24 * 60 * 60; // 24 horas
  private final UsuarioId id;
  private Username username;
  private Email email;
  private PasswordHash passwordHash;
  private Avatar avatar;
  private final Instant createdAt;
  private Instant updatedAt;
  private Rol role;
  private Idioma language;
  private boolean notificationsActive;
  private EstadoUsuario status;
  private DiscordUserId discordUserId;
  private DiscordUsername discordUsername;
  private Instant discordLinkedAt;
  private TokenVerificacion tokenVerificacion;
  private Instant tokenVerificacionExpiracion;
  private TokenVerificacion tokenRestablecimiento;
  private Instant tokenRestablecimientoExpiracion;

  private Usuario(Builder builder) {
    validarArgumentosCreacion(
        builder.username, builder.email, builder.passwordHash, builder.createdAt);
    this.id = builder.id;
    this.username = builder.username;
    this.email = builder.email;
    this.passwordHash = builder.passwordHash;
    this.avatar = builder.avatar != null ? builder.avatar : Avatar.empty();
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt != null ? builder.updatedAt : builder.createdAt;
    this.role = builder.role != null ? builder.role : Rol.USER;
    this.language = builder.language != null ? builder.language : Idioma.ESP;
    this.notificationsActive = builder.notificationsActive;
    this.status = builder.status != null ? builder.status : EstadoUsuario.ACTIVO;
    this.discordUserId =
        builder.discordUserId != null ? builder.discordUserId : DiscordUserId.empty();
    this.discordUsername =
        builder.discordUsername != null ? builder.discordUsername : DiscordUsername.empty();
    this.discordLinkedAt = builder.discordLinkedAt;
    this.tokenVerificacion =
        builder.tokenVerificacion != null ? builder.tokenVerificacion : TokenVerificacion.empty();
    this.tokenVerificacionExpiracion = builder.tokenVerificacionExpiracion;
    this.tokenRestablecimiento =
        builder.tokenRestablecimiento != null
            ? builder.tokenRestablecimiento
            : TokenVerificacion.empty();
    this.tokenRestablecimientoExpiracion = builder.tokenRestablecimientoExpiracion;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UsuarioId id;
    private Username username;
    private Email email;
    private PasswordHash passwordHash;
    private Avatar avatar;
    private Instant createdAt;
    private Instant updatedAt;
    private Rol role;
    private Idioma language;
    private boolean notificationsActive = true;
    private EstadoUsuario status;
    private DiscordUserId discordUserId;
    private DiscordUsername discordUsername;
    private Instant discordLinkedAt;
    private TokenVerificacion tokenVerificacion;
    private Instant tokenVerificacionExpiracion;
    private TokenVerificacion tokenRestablecimiento;
    private Instant tokenRestablecimientoExpiracion;

    private Builder() {}

    public Builder id(UsuarioId id) {
      this.id = id;
      return this;
    }

    public Builder username(Username username) {
      this.username = username;
      return this;
    }

    public Builder email(Email email) {
      this.email = email;
      return this;
    }

    public Builder passwordHash(PasswordHash passwordHash) {
      this.passwordHash = passwordHash;
      return this;
    }

    public Builder avatar(Avatar avatar) {
      this.avatar = avatar;
      return this;
    }

    public Builder createdAt(Instant createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(Instant updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder role(Rol role) {
      this.role = role;
      return this;
    }

    public Builder language(Idioma language) {
      this.language = language;
      return this;
    }

    public Builder notificationsActive(boolean notificationsActive) {
      this.notificationsActive = notificationsActive;
      return this;
    }

    public Builder status(EstadoUsuario status) {
      this.status = status;
      return this;
    }

    public Builder discordUserId(DiscordUserId discordUserId) {
      this.discordUserId = discordUserId;
      return this;
    }

    public Builder discordUsername(DiscordUsername discordUsername) {
      this.discordUsername = discordUsername;
      return this;
    }

    public Builder discordLinkedAt(Instant discordLinkedAt) {
      this.discordLinkedAt = discordLinkedAt;
      return this;
    }

    public Builder tokenVerificacion(TokenVerificacion tokenVerificacion) {
      this.tokenVerificacion = tokenVerificacion;
      return this;
    }

    public Builder tokenVerificacionExpiracion(Instant tokenVerificacionExpiracion) {
      this.tokenVerificacionExpiracion = tokenVerificacionExpiracion;
      return this;
    }

    public Builder tokenRestablecimiento(TokenVerificacion tokenRestablecimiento) {
      this.tokenRestablecimiento = tokenRestablecimiento;
      return this;
    }

    public Builder tokenRestablecimientoExpiracion(Instant tokenRestablecimientoExpiracion) {
      this.tokenRestablecimientoExpiracion = tokenRestablecimientoExpiracion;
      return this;
    }

    public Usuario build() {
      return new Usuario(this);
    }
  }

  public static Usuario create(Username username, Email email, PasswordHash passwordHash) {
    Usuario usuario =
        Usuario.builder()
            .id(UsuarioId.generate())
            .username(username)
            .email(email)
            .passwordHash(passwordHash)
            .avatar(Avatar.empty())
            .createdAt(Instant.now())
            .role(Rol.USER)
            .language(Idioma.ESP)
            .status(EstadoUsuario.PENDIENTE_DE_VERIFICACION)
            .notificationsActive(true)
            .build();
    usuario.generarTokenVerificacion();
    return usuario;
  }

  @SuppressWarnings(
      "java:S107") // Reconstitución desde persistencia requiere todos los parámetros del aggregate
  public static Usuario reconstitute(
      UsuarioId id,
      Username username,
      Email email,
      PasswordHash passwordHash,
      Avatar avatar,
      Instant createdAt,
      Instant updatedAt,
      Rol role,
      Idioma language,
      boolean notificationsActive,
      EstadoUsuario status,
      DiscordUserId discordUserId,
      DiscordUsername discordUsername,
      Instant discordLinkedAt,
      TokenVerificacion tokenVerificacion,
      Instant tokenVerificacionExpiracion,
      TokenVerificacion tokenRestablecimiento,
      Instant tokenRestablecimientoExpiracion) {
    return Usuario.builder()
        .id(id)
        .username(username)
        .email(email)
        .passwordHash(passwordHash)
        .avatar(avatar)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .role(role)
        .language(language)
        .notificationsActive(notificationsActive)
        .status(status)
        .discordUserId(discordUserId)
        .discordUsername(discordUsername)
        .discordLinkedAt(discordLinkedAt)
        .tokenVerificacion(tokenVerificacion)
        .tokenVerificacionExpiracion(tokenVerificacionExpiracion)
        .tokenRestablecimiento(tokenRestablecimiento)
        .tokenRestablecimientoExpiracion(tokenRestablecimientoExpiracion)
        .build();
  }

  private void validarArgumentosCreacion(
      Username username, Email email, PasswordHash passwordHash, Instant createdAt) {
    if (username == null) {
      throw new IllegalArgumentException("El username es obligatorio");
    }
    if (email == null) {
      throw new IllegalArgumentException("El email es obligatorio");
    }
    if (passwordHash == null) {
      throw new IllegalArgumentException("El password hash es obligatorio");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("La fecha de creación es obligatoria");
    }
  }

  public void changeEmail(Email newEmail) {
    if (newEmail == null) {
      throw new IllegalArgumentException("El email no puede ser nulo");
    }
    this.email = newEmail;
    this.updatedAt = Instant.now();
  }

  public void changePasswordHash(PasswordHash newPasswordHash) {
    if (newPasswordHash == null) {
      throw new IllegalArgumentException("El password hash no puede ser nulo");
    }
    this.passwordHash = newPasswordHash;
    this.updatedAt = Instant.now();
  }

  public void changeAvatar(Avatar newAvatar) {
    this.avatar = newAvatar != null ? newAvatar : Avatar.empty();
    this.updatedAt = Instant.now();
  }

  public void changeLanguage(Idioma newLanguage) {
    this.language = newLanguage != null ? newLanguage : Idioma.ESP;
    this.updatedAt = Instant.now();
  }

  public void enableNotifications() {
    this.notificationsActive = true;
    this.updatedAt = Instant.now();
  }

  public void disableNotifications() {
    this.notificationsActive = false;
    this.updatedAt = Instant.now();
  }

  public void suspend() {
    this.status = EstadoUsuario.SUSPENDIDO;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    if (this.status == EstadoUsuario.ELIMINADO) {
      throw new IllegalStateException("No se puede activar un usuario eliminado");
    }
    this.status = EstadoUsuario.ACTIVO;
    this.updatedAt = Instant.now();
  }

  public void delete() {
    this.status = EstadoUsuario.ELIMINADO;
    this.updatedAt = Instant.now();
  }

  public void marcarPendienteVerificacion() {
    this.status = EstadoUsuario.PENDIENTE_DE_VERIFICACION;
    this.updatedAt = Instant.now();
  }

  public void generarTokenVerificacion() {
    this.tokenVerificacion = TokenVerificacion.generate();
    this.tokenVerificacionExpiracion =
        Instant.now().plusSeconds(EMAIL_VERIFICATION_TOKEN_TTL_SECONDS);
    this.updatedAt = Instant.now();
  }

  public void generarTokenRestablecimiento() {
    this.tokenRestablecimiento = TokenVerificacion.generate();
    this.tokenRestablecimientoExpiracion =
        Instant.now().plusSeconds(PASSWORD_RESET_TOKEN_TTL_SECONDS);
    this.updatedAt = Instant.now();
  }

  public boolean tieneTokenRestablecimientoValido(TokenVerificacion token) {
    if (this.tokenRestablecimiento == null || this.tokenRestablecimiento.isEmpty()) {
      return false;
    }
    if (this.tokenRestablecimientoExpiracion == null
        || Instant.now().isAfter(this.tokenRestablecimientoExpiracion)) {
      return false;
    }
    return this.tokenRestablecimiento.equals(token);
  }

  public void invalidarTokenRestablecimiento() {
    this.tokenRestablecimiento = TokenVerificacion.empty();
    this.tokenRestablecimientoExpiracion = null;
    this.updatedAt = Instant.now();
  }

  public void verificarEmail(TokenVerificacion token) {
    if (this.status != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
      throw new IllegalStateException("El usuario ya ha sido verificado");
    }
    if (this.tokenVerificacion == null || this.tokenVerificacion.isEmpty()) {
      throw new IllegalArgumentException("No hay token de verificación pendiente");
    }
    if (!this.tokenVerificacion.equals(token)) {
      throw new IllegalArgumentException("El token de verificación no es válido");
    }
    if (this.tokenVerificacionExpiracion == null
        || Instant.now().isAfter(this.tokenVerificacionExpiracion)) {
      throw new IllegalArgumentException("El token de verificación ha expirado");
    }

    this.status = EstadoUsuario.ACTIVO;
    this.tokenVerificacion = TokenVerificacion.empty();
    this.tokenVerificacionExpiracion = null;
    this.updatedAt = Instant.now();
  }

  public void linkDiscord(DiscordUserId discordUserId, DiscordUsername discordUsername) {
    if (discordUserId == null || discordUserId.isEmpty()) {
      throw new IllegalArgumentException("El ID de Discord no puede ser nulo o vacío");
    }
    if (discordUsername == null || discordUsername.isEmpty()) {
      throw new IllegalArgumentException("El username de Discord no puede ser nulo o vacío");
    }
    this.discordUserId = discordUserId;
    this.discordUsername = discordUsername;
    this.discordLinkedAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public void unlinkDiscord() {
    this.discordUserId = DiscordUserId.empty();
    this.discordUsername = DiscordUsername.empty();
    this.discordLinkedAt = null;
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return this.status == EstadoUsuario.ACTIVO;
  }

  public boolean isSuspended() {
    return this.status == EstadoUsuario.SUSPENDIDO;
  }

  public boolean isDeleted() {
    return this.status == EstadoUsuario.ELIMINADO;
  }

  public boolean hasDiscordLinked() {
    return !this.discordUserId.isEmpty() && !this.discordUsername.isEmpty();
  }

  // Getters
  public UsuarioId getId() {
    return id;
  }

  public Username getUsername() {
    return username;
  }

  public Email getEmail() {
    return email;
  }

  public PasswordHash getPasswordHash() {
    return passwordHash;
  }

  public Avatar getAvatar() {
    return avatar;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Rol getRole() {
    return role;
  }

  public Idioma getLanguage() {
    return language;
  }

  public boolean isNotificationsActive() {
    return notificationsActive;
  }

  public EstadoUsuario getStatus() {
    return status;
  }

  public DiscordUserId getDiscordUserId() {
    return discordUserId;
  }

  public DiscordUsername getDiscordUsername() {
    return discordUsername;
  }

  public Instant getDiscordLinkedAt() {
    return discordLinkedAt;
  }

  public TokenVerificacion getTokenVerificacion() {
    return tokenVerificacion;
  }

  public Instant getTokenVerificacionExpiracion() {
    return tokenVerificacionExpiracion;
  }

  public TokenVerificacion getTokenRestablecimiento() {
    return tokenRestablecimiento;
  }

  public Instant getTokenRestablecimientoExpiracion() {
    return tokenRestablecimientoExpiracion;
  }

  @Override
  public String toString() {
    return "Usuario{"
        + "id="
        + id
        + ", username="
        + username
        + ", email="
        + email
        + ", status="
        + status
        + '}';
  }
}
