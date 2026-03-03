package com.gamelisto.biblioteca.infrastructure.out.persistence;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class ListaGameItemId implements Serializable {
  private UUID listaId;
  private Long gameRefId;

  public ListaGameItemId() {}

  public ListaGameItemId(UUID listaId, Long gameRefId) {
    this.listaId = listaId;
    this.gameRefId = gameRefId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ListaGameItemId that = (ListaGameItemId) o;
    return Objects.equals(listaId, that.listaId) && Objects.equals(gameRefId, that.gameRefId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(listaId, gameRefId);
  }
}
