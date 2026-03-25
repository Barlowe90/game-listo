package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.UsuarioRef;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRefMapper {

  public UsuarioRefDocument toEntity(UsuarioRef usuarioRef) {
    UsuarioRefDocument entity = new UsuarioRefDocument();
    entity.setId(usuarioRef.getId());
    entity.setUsername(usuarioRef.getUsername());
    entity.setAvatar(usuarioRef.getAvatar());
    entity.setDiscordUserId(usuarioRef.getDiscordUserId());
    return entity;
  }

  public UsuarioRef toDomain(UsuarioRefDocument entity) {
    return UsuarioRef.reconstitute(
        entity.getId(), entity.getUsername(), entity.getAvatar(), entity.getDiscordUserId());
  }
}
