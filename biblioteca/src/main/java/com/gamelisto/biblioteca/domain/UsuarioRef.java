package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import com.gamelisto.biblioteca.domain.UsuarioId;

@Getter
@ToString
public class UsuarioRef {
  private final UsuarioId id;
  private final String username;
  private final String rol;
  private final String avatar;
  private final List<ListaGame> listas;
  private final List<GameEstado> juegos;

  private UsuarioRef(UsuarioId id, String username, String avatar, String rol) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listas = new ArrayList<>();
    this.juegos = new ArrayList<>();
  }

  public static UsuarioRef reconstitute(UsuarioId id, String username, String avatar, String rol) {
    return new UsuarioRef(id, username, avatar, rol);
  }

  public void addNuevaLista(ListaGame listaNueva) {
    listas.add(listaNueva);
  }

  public void addGameEstado(GameEstado gameEstado) {
    juegos.add(gameEstado);
  }
}
