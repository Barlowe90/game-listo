package com.gamelisto.biblioteca.domain.listas;

import com.gamelisto.biblioteca.domain.gameEstado.GameEstado;
import com.gamelisto.biblioteca.domain.gameRef.GameRef;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class ListaGame {
  private final ListaGameId id;
  private final UUID usuarioRefId;
  private final NombreListaGame nombreLista;
  private final Tipo tipo;
  private final List<GameEstado> listaGameEstados;
  private final List<GameRef> listaGameRefs;

  private ListaGame(Builder builder) {
    this.id = builder.id;
    this.usuarioRefId = builder.usuarioRefId;
    this.nombreLista = builder.nombreLista;
    this.tipo = builder.tipo;
    this.listaGameEstados = new ArrayList<>();
    this.listaGameRefs = new ArrayList<>();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ListaGameId id;
    private UUID usuarioRefId;
    private NombreListaGame nombreLista;
    private Tipo tipo;

    private Builder() {}

    public Builder id(ListaGameId id) {
      this.id = id;
      return this;
    }

    public Builder usuarioRefId(UUID usuarioRefId) {
      this.usuarioRefId = usuarioRefId;
      return this;
    }

    public Builder nombreLista(NombreListaGame nombreLista) {
      this.nombreLista = nombreLista;
      return this;
    }

    public Builder tipo(Tipo tipo) {
      this.tipo = tipo;
      return this;
    }

    public ListaGame build() {
      return new ListaGame(this);
    }
  }

  public static ListaGame create(UUID usuarioRefId, NombreListaGame nombreLista, Tipo tipo) {
    return ListaGame.builder()
        .id(ListaGameId.generate())
        .usuarioRefId(usuarioRefId)
        .nombreLista(nombreLista)
        .tipo(tipo)
        .build();
  }

  public static ListaGame reconstitute(
      ListaGameId id, UUID usuarioRefId, NombreListaGame nombreLista, Tipo tipo) {
    return ListaGame.builder()
        .id(id)
        .usuarioRefId(usuarioRefId)
        .nombreLista(nombreLista)
        .tipo(tipo)
        .build();
  }

  public void addGameEstado(GameEstado gameEstado) {
    listaGameEstados.add(gameEstado);
  }

  public void addGameRef(GameRef gameRef) {
    listaGameRefs.add(gameRef);
  }

  public List<GameEstado> getListaGameEstados() {
    return Collections.unmodifiableList(listaGameEstados);
  }

  public List<GameRef> getListaGameRefs() {
    return Collections.unmodifiableList(listaGameRefs);
  }
}
