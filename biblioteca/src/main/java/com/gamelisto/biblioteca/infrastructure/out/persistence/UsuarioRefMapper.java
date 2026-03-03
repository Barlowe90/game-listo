package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.UsuarioRef;
import com.gamelisto.biblioteca.domain.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRefMapper {

  public UsuarioRefEntity toEntity(UsuarioRef usuarioRef) {
    UsuarioRefEntity entity = new UsuarioRefEntity();
    entity.setId(usuarioRef.getId().value());
    entity.setUsername(usuarioRef.getUsername());
    entity.setRol(usuarioRef.getRol());
    entity.setAvatar(usuarioRef.getAvatar());
    return entity;
  }

  public UsuarioRef toDomain(UsuarioRefEntity entity) {
    return UsuarioRef.reconstitute(
        UsuarioId.of(entity.getId()), entity.getUsername(), entity.getAvatar(), entity.getRol());
  }
}
