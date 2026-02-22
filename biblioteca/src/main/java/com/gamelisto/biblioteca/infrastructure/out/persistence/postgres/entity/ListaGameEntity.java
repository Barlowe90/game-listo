package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity;

import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.listas.Visibilidad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lista_game")
public class ListaGameEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "usuario_ref_id", nullable = false, updatable = false)
  private String usuarioRefId;

  @Column(name = "nombre_lista", nullable = false, updatable = false)
  private String nombreLista;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 100)
  private Tipo tipo;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibilidad", nullable = false, length = 100)
  private Visibilidad visibilidad;

  public ListaGameEntity() {
    // vacio para jpa
  }
}
