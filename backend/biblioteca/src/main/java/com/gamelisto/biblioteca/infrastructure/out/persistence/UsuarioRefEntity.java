package com.gamelisto.biblioteca.infrastructure.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "usuarios_ref")
public class UsuarioRefEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "username", nullable = false, unique = true, length = 30)
  private String username;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "discord_user_id", length = 100)
  private String discordUserId;

  @Column(name = "discord_username", length = 100)
  private String discordUsername;

  @OneToMany(mappedBy = "usuarioRef", fetch = FetchType.LAZY)
  private List<ListaGameEntity> listas = new ArrayList<>();

  @OneToMany(mappedBy = "usuarioRef", fetch = FetchType.LAZY)
  private List<GameEstadoEntity> juegos = new ArrayList<>();

  public UsuarioRefEntity() {
    // constructor vacio requerido por jpa para instanciacion via reflection
  }
}
