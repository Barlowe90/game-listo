package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity;

import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Idioma;
import com.gamelisto.usuarios_service.domain.usuario.Rol;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "username", nullable = false, unique = true, length = 30)
  private String username;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "avatar", length = 500)
  private String avatar;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private Rol role;

  @Enumerated(EnumType.STRING)
  @Column(name = "language", nullable = false, length = 3)
  private Idioma language;

  @Column(name = "is_notifications_active", nullable = false)
  private boolean notificationsActive;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private EstadoUsuario status;

  @Column(name = "id_user_discord", length = 100)
  private String discordUserId;

  @Column(name = "discord_username", length = 100)
  private String discordUsername;

  @Column(name = "discord_linked_at")
  private Instant discordLinkedAt;

  @Column(name = "token_verificacion", length = 100)
  private String tokenVerificacion;

  @Column(name = "token_verificacion_expiracion")
  private Instant tokenVerificacionExpiracion;

  @Column(name = "token_restablecimiento", length = 100)
  private String tokenRestablecimiento;

  @Column(name = "token_restablecimiento_expiracion")
  private Instant tokenRestablecimientoExpiracion;

  /** Constructor sin argumentos requerido por JPA. */
  public UsuarioEntity() {
    // Constructor vacío requerido por JPA/Hibernate para instanciación vía reflection
  }

  // Getters and setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Rol getRole() {
    return role;
  }

  public void setRole(Rol role) {
    this.role = role;
  }

  public Idioma getLanguage() {
    return language;
  }

  public void setLanguage(Idioma language) {
    this.language = language;
  }

  public boolean isNotificationsActive() {
    return notificationsActive;
  }

  public void setNotificationsActive(boolean notificationsActive) {
    this.notificationsActive = notificationsActive;
  }

  public EstadoUsuario getStatus() {
    return status;
  }

  public void setStatus(EstadoUsuario status) {
    this.status = status;
  }

  public String getDiscordUserId() {
    return discordUserId;
  }

  public void setDiscordUserId(String discordUserId) {
    this.discordUserId = discordUserId;
  }

  public String getDiscordUsername() {
    return discordUsername;
  }

  public void setDiscordUsername(String discordUsername) {
    this.discordUsername = discordUsername;
  }

  public Instant getDiscordLinkedAt() {
    return discordLinkedAt;
  }

  public void setDiscordLinkedAt(Instant discordLinkedAt) {
    this.discordLinkedAt = discordLinkedAt;
  }

  public String getTokenVerificacion() {
    return tokenVerificacion;
  }

  public void setTokenVerificacion(String tokenVerificacion) {
    this.tokenVerificacion = tokenVerificacion;
  }

  public Instant getTokenVerificacionExpiracion() {
    return tokenVerificacionExpiracion;
  }

  public void setTokenVerificacionExpiracion(Instant tokenVerificacionExpiracion) {
    this.tokenVerificacionExpiracion = tokenVerificacionExpiracion;
  }

  public String getTokenRestablecimiento() {
    return tokenRestablecimiento;
  }

  public void setTokenRestablecimiento(String tokenRestablecimiento) {
    this.tokenRestablecimiento = tokenRestablecimiento;
  }

  public Instant getTokenRestablecimientoExpiracion() {
    return tokenRestablecimientoExpiracion;
  }

  public void setTokenRestablecimientoExpiracion(Instant tokenRestablecimientoExpiracion) {
    this.tokenRestablecimientoExpiracion = tokenRestablecimientoExpiracion;
  }
}
