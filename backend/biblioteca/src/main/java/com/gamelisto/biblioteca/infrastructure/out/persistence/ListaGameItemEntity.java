package com.gamelisto.biblioteca.infrastructure.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lista_game_item")
@IdClass(ListaGameItemId.class)
@Getter
@Setter
public class ListaGameItemEntity {

  @Id
  @Column(name = "lista_id", nullable = false, updatable = false)
  private java.util.UUID listaId;

  @Id
  @Column(name = "game_ref_id", nullable = false, updatable = false)
  private Long gameRefId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lista_id", nullable = false, insertable = false, updatable = false)
  private ListaGameEntity lista;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_ref_id", nullable = false, insertable = false, updatable = false)
  private GameRefEntity gameRef;
}
