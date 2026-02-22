package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "game_ref")
public class GameRefEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "game_ref_id", nullable = false, unique = true, length = 100)
  private String gameRefId;

  @Column(name = "nombre", nullable = false, unique = true, length = 100)
  private String nombre;

  @Column(name = "cover", nullable = false, unique = true, length = 500)
  private String cover;

  public GameRefEntity() {
    // vacio jpa
  }
}
