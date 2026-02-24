package com.gamelisto.biblioteca.domain.usuario;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import com.gamelisto.biblioteca.domain.gameestado.GameEstado;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
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
  private final List<ListaGame> listas;
  private final List<GameEstado> juegos;

  private UsuarioRef(String username, String avatar, String rol) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listas = new ArrayList<>();
    this.juegos = new ArrayList<>();
  }

  // Reconstitución con id
  private UsuarioRef(UUID id, String username, String avatar, String rol) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.rol = rol;
    this.listas = new ArrayList<>();
    this.juegos = new ArrayList<>();
  }

  public static UsuarioRef create(String username, String avatar, String rol) {
    return new UsuarioRef(username, avatar, rol);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar, String rol) {
    return new UsuarioRef(id, username, avatar, rol);
  }

  public void addNuevaLista(ListaGame listaNueva) {
    listas.add(listaNueva);
  }

  public void eliminarLista(ListaGame lista) {
    if (lista.getTipo().equals(Tipo.OFICIAL)) {
      throw new DomainException("No se puede eliminar una lista predefinida");
    }
    listas.remove(lista);
  }

  public void addNuevoJuego(GameEstado gameEstado) {
    juegos.add(gameEstado);
  }

  public void eliminarJuego(GameEstado gameEstado) {
    juegos.remove(gameEstado);
  }
}
