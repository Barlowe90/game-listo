package com.gamelisto.usuarios.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.entity.UsuarioEntity;
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
    entity.setRole(usuario.getRole());
    entity.setLanguage(usuario.getLanguage());
    entity.setStatus(usuario.getStatus());
    entity.setDiscordUserId(
        usuario.getDiscordUserId().isEmpty() ? null : usuario.getDiscordUserId().value());
    entity.setDiscordUsername(
        usuario.getDiscordUsername().isEmpty() ? null : usuario.getDiscordUsername().value());
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
        entity.getRole(),
        entity.getLanguage(),
        entity.getStatus(),
        entity.getDiscordUserId() != null
            ? DiscordUserId.of(entity.getDiscordUserId())
            : DiscordUserId.empty(),
        entity.getDiscordUsername() != null
            ? DiscordUsername.of(entity.getDiscordUsername())
            : DiscordUsername.empty(),
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
