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

  @Column(name = "usuario_ref_id", nullable = false)
  private UUID usuarioRefId;

  @Column(name = "game_ref_id", nullable = false)
  private UUID gameRefId;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;

  @Column(name = "rating")
  private double rating;

  public GameEstadoEntity() {
    // vacio jpa
  }
}
