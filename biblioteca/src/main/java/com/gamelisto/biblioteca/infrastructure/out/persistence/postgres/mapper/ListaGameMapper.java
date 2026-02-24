package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.ListaGameEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.UsuarioRefEntity;
import org.springframework.stereotype.Component;

@Component
public class ListaGameMapper {

  public ListaGameEntity toEntity(ListaGame listaGame) {
    ListaGameEntity entity = new ListaGameEntity();
    entity.setId(listaGame.getId().value());
    // mapear usuarioRefId creando una referencia ligera a UsuarioRefEntity
    UsuarioRefEntity usuarioRef = new UsuarioRefEntity();
    usuarioRef.setId(listaGame.getUsuarioRefId());
    entity.setUsuarioRef(usuarioRef);
    entity.setNombreLista(listaGame.getNombreLista().value());
    entity.setTipo(listaGame.getTipo());
    return entity;
  }

  public ListaGame toDomain(ListaGameEntity entity) {
    return ListaGame.reconstitute(
        ListaGameId.of(entity.getId()),
        entity.getUsuarioRef().getId(),
        NombreListaGame.of(entity.getNombreLista()),
        entity.getTipo());
  }
}
