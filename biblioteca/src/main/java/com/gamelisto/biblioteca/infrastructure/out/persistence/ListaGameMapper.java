package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ListaGameMapper {

  public ListaGameEntity toEntity(ListaGame listaGame) {
    ListaGameEntity entity = new ListaGameEntity();
    entity.setId(listaGame.getId().value());
    entity.setNombreLista(listaGame.getNombreLista().value());
    entity.setTipo(listaGame.getTipo());
    return entity;
  }

  public ListaGame toDomain(ListaGameEntity entity) {
    UUID usuarioId = entity.getUsuarioRef().getId();

    return ListaGame.reconstitute(
        ListaGameId.of(entity.getId()),
        usuarioId,
        NombreListaGame.of(entity.getNombreLista()),
        entity.getTipo());
  }
}
