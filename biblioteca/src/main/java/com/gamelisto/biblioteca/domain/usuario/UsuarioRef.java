package com.gamelisto.biblioteca.domain.usuario;

import com.gamelisto.biblioteca.domain.gameestado.GameEstado;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class UsuarioRef {
  private final UUID id;
  private final String username;
  private final String rol;
  private final String avatar;
  private final List<ListaGame> listasDeJuegos;
  private final List<GameEstado> listaGameEstados;

  private UsuarioRef(String username, String avatar, String rol) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listasDeJuegos = new ArrayList<>();
    this.listaGameEstados = new ArrayList<>();
  }

  // Reconstitución con id
  private UsuarioRef(UUID id, String username, String avatar, String rol) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listasDeJuegos = new ArrayList<>();
    this.listaGameEstados = new ArrayList<>();
  }

  public static UsuarioRef create(String username, String avatar, String rol) {
    return new UsuarioRef(username, avatar, rol);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar, String rol) {
    return new UsuarioRef(id, username, avatar, rol);
  }

  public void addNewList(ListaGame listaNueva) {
    listasDeJuegos.add(listaNueva);
  }

  public void addNewGameEstado(GameEstado gameEstado) {
    listaGameEstados.add(gameEstado);
  }

  public List<ListaGame> getListasDeJuegos() {
    return Collections.unmodifiableList(listasDeJuegos);
  }

  public List<GameEstado> getListaGameEstados() {
    return Collections.unmodifiableList(listaGameEstados);
  }
}
