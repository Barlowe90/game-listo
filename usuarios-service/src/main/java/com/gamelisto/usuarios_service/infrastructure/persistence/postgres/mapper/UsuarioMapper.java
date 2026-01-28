package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.mapper;

import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  public UsuarioEntity toEntity(Usuario usuario) {
    UsuarioEntity entity = new UsuarioEntity();
    entity.setId(usuario.getId().value());
    entity.setUsername(usuario.getUsername().value());
    entity.setEmail(usuario.getEmail().value());
    entity.setPasswordHash(usuario.getPasswordHash().value());
    entity.setAvatar(usuario.getAvatar().isEmpty() ? null : usuario.getAvatar().url());
    entity.setCreatedAt(usuario.getCreatedAt());
    entity.setUpdatedAt(usuario.getUpdatedAt());
    entity.setRole(usuario.getRole());
    entity.setLanguage(usuario.getLanguage());
    entity.setNotificationsActive(usuario.isNotificationsActive());
    entity.setStatus(usuario.getStatus());
    entity.setDiscordUserId(
        usuario.getDiscordUserId().isEmpty() ? null : usuario.getDiscordUserId().value());
    entity.setDiscordUsername(
        usuario.getDiscordUsername().isEmpty() ? null : usuario.getDiscordUsername().value());
    entity.setDiscordLinkedAt(usuario.getDiscordLinkedAt());
    entity.setTokenVerificacion(
        usuario.getTokenVerificacion() != null && !usuario.getTokenVerificacion().isEmpty()
            ? usuario.getTokenVerificacion().value()
            : null);
    entity.setTokenVerificacionExpiracion(usuario.getTokenVerificacionExpiracion());
    entity.setTokenRestablecimiento(
        usuario.getTokenRestablecimiento() != null && !usuario.getTokenRestablecimiento().isEmpty()
            ? usuario.getTokenRestablecimiento().value()
            : null);
    entity.setTokenRestablecimientoExpiracion(usuario.getTokenRestablecimientoExpiracion());
    return entity;
  }

  public Usuario toDomain(UsuarioEntity entity) {
    return Usuario.reconstitute(
        UsuarioId.of(entity.getId()),
        Username.of(entity.getUsername()),
        Email.of(entity.getEmail()),
        PasswordHash.of(entity.getPasswordHash()),
        entity.getAvatar() != null ? Avatar.of(entity.getAvatar()) : Avatar.empty(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getRole(),
        entity.getLanguage(),
        entity.isNotificationsActive(),
        entity.getStatus(),
        entity.getDiscordUserId() != null
            ? DiscordUserId.of(entity.getDiscordUserId())
            : DiscordUserId.empty(),
        entity.getDiscordUsername() != null
            ? DiscordUsername.of(entity.getDiscordUsername())
            : DiscordUsername.empty(),
        entity.getDiscordLinkedAt(),
        entity.getTokenVerificacion() != null
            ? TokenVerificacion.of(entity.getTokenVerificacion())
            : TokenVerificacion.empty(),
        entity.getTokenVerificacionExpiracion(),
        entity.getTokenRestablecimiento() != null
            ? TokenVerificacion.of(entity.getTokenRestablecimiento())
            : TokenVerificacion.empty(),
        entity.getTokenRestablecimientoExpiracion());
  }
}
