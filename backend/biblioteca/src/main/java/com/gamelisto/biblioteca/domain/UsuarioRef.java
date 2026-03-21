package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import com.gamelisto.biblioteca.domain.exceptions.DomainException;

@Getter
@ToString
public class UsuarioRef {
  private final UsuarioId id;
  private final String username;
  private final String avatar;
  private final String discordUserId;
  private final String discordUsername;
  private final List<ListaGame> listas;
  private final List<GameEstado> juegos;

  private UsuarioRef(
      UsuarioId id,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.discordUserId = discordUserId;
    this.discordUsername = discordUsername;
    this.listas = new ArrayList<>();
    this.juegos = new ArrayList<>();
  }

  public static UsuarioRef reconstitute(UsuarioId id, String username, String avatar) {
    return reconstitute(id, username, avatar, null, null);
  }

  public static UsuarioRef create(UsuarioId id, String username, String avatar) {
    return create(id, username, avatar, null, null);
  }

  public static UsuarioRef reconstitute(
      UsuarioId id,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername) {
    return new UsuarioRef(id, username, avatar, discordUserId, discordUsername);
  }

  public static UsuarioRef create(
      UsuarioId id,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername) {
    comprobarIdUsernameVacios(id, username);
    String safeAvatar = avatar == null ? "" : avatar;

    return new UsuarioRef(id, username, safeAvatar, discordUserId, discordUsername);
  }

  private static void comprobarIdUsernameVacios(UsuarioId id, String username) {
    if (id == null) {
      throw new DomainException("UsuarioId no puede ser nulo");
    }
    if (username == null || username.isBlank()) {
      throw new DomainException("username no puede ser nulo o vacío");
    }
  }

  public void addNuevaLista(ListaGame listaNueva) {
    listas.add(listaNueva);
  }

  public void addGameEstado(GameEstado gameEstado) {
    juegos.add(gameEstado);
  }
}
