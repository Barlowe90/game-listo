package com.gamelisto.biblioteca.domain;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class ListaGame {
  private final ListaGameId id;
  private final UUID usuarioRefId;
  private NombreListaGame nombreLista;
  private final Tipo tipo;

  private ListaGame(Builder builder) {
    this.id = builder.id;
    this.usuarioRefId = builder.usuarioRefId;
    this.nombreLista = builder.nombreLista;
    this.tipo = builder.tipo;
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
    comprobarCamposNulos(usuarioRefId, nombreLista, tipo);

    return ListaGame.builder()
        .id(ListaGameId.generate())
        .usuarioRefId(usuarioRefId)
        .nombreLista(nombreLista)
        .tipo(tipo)
        .build();
  }

  private static void comprobarCamposNulos(
      UUID usuarioRefId, NombreListaGame nombreLista, Tipo tipo) {
    if (usuarioRefId == null) {
      throw new DomainException("El usuarioRefId no puede ser nulo.");
    }
    if (nombreLista == null) {
      throw new DomainException("El nombre de la lista no puede ser nulo.");
    }
    if (tipo == null) {
      throw new DomainException("El tipo no puede ser nulo.");
    }
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

  public void cambiarNombre(NombreListaGame nuevoNombreLista) {
    if (nuevoNombreLista == null) {
      throw new DomainException("El nuevo nombre no puede ser nulo.");
    }

    if (!this.nombreLista.equals(nuevoNombreLista)) {
      this.nombreLista = nuevoNombreLista;
    }
  }
}
