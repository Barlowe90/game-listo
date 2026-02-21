package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ListaGame {
  private final ListaGameId id;
  private final String usuarioRefId;
  private final NombreListaGame nombreLista;
  private final Tipo tipo;
  private final Visibilidad visibilidad;

  private ListaGame(Builder builder) {
    this.id = builder.id;
    this.usuarioRefId = builder.usuarioRefId;
    this.nombreLista = builder.nombreLista;
    this.tipo = builder.tipo;
    this.visibilidad = builder.visibilidad;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ListaGameId id;
    private String usuarioRefId;
    private NombreListaGame nombreLista;
    private Tipo tipo;
    private Visibilidad visibilidad;

    private Builder() {}

    public Builder id(ListaGameId id) {
      this.id = id;
      return this;
    }

    public Builder usuarioRefId(String usuarioRefId) {
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

    public Builder visibilidad(Visibilidad visibilidad) {
      this.visibilidad = visibilidad;
      return this;
    }

    public ListaGame build() {
      return new ListaGame(this);
    }
  }

  public static ListaGame create(
      String usuarioRefId, NombreListaGame nombreLista, Tipo tipo, Visibilidad visibilidad) {
    return ListaGame.builder()
        .id(ListaGameId.generate())
        .usuarioRefId(usuarioRefId)
        .nombreLista(nombreLista)
        .tipo(tipo)
        .visibilidad(visibilidad)
        .build();
  }

  public static ListaGame reconstitute(
      ListaGameId id,
      String usuarioRefId,
      NombreListaGame nombreLista,
      Tipo tipo,
      Visibilidad visibilidad) {
    return ListaGame.builder()
        .id(id)
        .usuarioRefId(usuarioRefId)
        .nombreLista(nombreLista)
        .tipo(tipo)
        .visibilidad(visibilidad)
        .build();
  }
}
