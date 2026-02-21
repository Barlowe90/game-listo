package com.gamelisto.usuarios.infrastructure.out.persistence.postgres.entity;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Rol;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

  @Column(name = "token_verificacion", length = 100)
  private String tokenVerificacion;

  @Column(name = "token_verificacion_expiracion")
  private Instant tokenVerificacionExpiracion;

  @Column(name = "token_restablecimiento", length = 100)
  private String tokenRestablecimiento;

  @Column(name = "token_restablecimiento_expiracion")
  private Instant tokenRestablecimientoExpiracion;

  public UsuarioEntity() {
    // Constructor vacío requerido por JPA/Hibernate para instanciación vía reflection
  }
}
