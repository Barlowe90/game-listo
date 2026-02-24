package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity;

import com.gamelisto.biblioteca.domain.gameestado.Estado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "game_estado")
public class GameEstadoEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_ref_id", nullable = false)
  private UsuarioRefEntity usuarioRef;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_ref_id", nullable = false)
  private GameRefEntity gameRef;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lista_id", nullable = true)
  private ListaGameEntity lista;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;

  @Column(name = "rating")
  private double rating;

  public GameEstadoEntity() {
    // vacio jpa
  }
}
