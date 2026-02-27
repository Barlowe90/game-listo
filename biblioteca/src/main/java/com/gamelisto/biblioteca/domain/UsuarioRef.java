package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class UsuarioRef {
  private final UUID id;
  private final String username;
  private final String rol;
  private final String avatar;
  private final List<ListaGame> listas;
  private final List<GameEstado> juegos;

  private UsuarioRef(UUID id, String username, String avatar, String rol) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listas = new ArrayList<>();
    this.juegos = new ArrayList<>();
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar, String rol) {
    return new UsuarioRef(id, username, avatar, rol);
  }

  public void addNuevaLista(ListaGame listaNueva) {
    listas.add(listaNueva);
  }

  public void addGameEstado(GameEstado gameEstado) {
    juegos.add(gameEstado);
  }
}
