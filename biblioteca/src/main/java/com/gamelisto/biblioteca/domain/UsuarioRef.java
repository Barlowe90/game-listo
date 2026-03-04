package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

  public static UsuarioRef create(UsuarioId id, String username, String avatar, String rol) {
    comprobarIdUsernameVacios(id, username);
    String safeAvatar = avatar == null ? "" : avatar;
    String safeRol = rol == null ? "" : rol;

    return new UsuarioRef(id, username, safeAvatar, safeRol);
  }

  private static void comprobarIdUsernameVacios(UsuarioId id, String username) {
    if (id == null) {
      throw new IllegalArgumentException("UsuarioId no puede ser nulo");
    }
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("username no puede ser nulo o vacío");
    }
  }

  public void addNuevaLista(ListaGame listaNueva) {
    listas.add(listaNueva);
  }

  public void addGameEstado(GameEstado gameEstado) {
    juegos.add(gameEstado);
  }
}
