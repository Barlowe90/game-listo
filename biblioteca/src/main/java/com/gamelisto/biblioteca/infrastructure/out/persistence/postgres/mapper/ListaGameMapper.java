package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.ListaGameEntity;
import org.springframework.stereotype.Component;

@Component
public class ListaGameMapper {

  public ListaGameEntity toEntity(ListaGame listaGame) {
    ListaGameEntity entity = new ListaGameEntity();
    entity.setId(listaGame.getId().value());
    entity.setUsuarioRefId(listaGame.getUsuarioRefId());
    entity.setNombreLista(listaGame.getNombreLista().value());
    entity.setTipo(listaGame.getTipo());
    entity.setVisibilidad(listaGame.getVisibilidad());
    return entity;
  }

  public ListaGame toDomain(ListaGameEntity entity) {
    return ListaGame.reconstitute(
        ListaGameId.of(entity.getId()),
        entity.getUsuarioRefId(),
        NombreListaGame.of(entity.getNombreLista()),
        entity.getTipo(),
        entity.getVisibilidad());
  }
}
