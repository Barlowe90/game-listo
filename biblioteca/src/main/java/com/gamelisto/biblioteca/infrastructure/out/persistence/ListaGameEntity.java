package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.Tipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lista_game")
public class ListaGameEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_ref_id", nullable = false, updatable = false)
  private UsuarioRefEntity usuarioRef;

  @Column(name = "nombre_lista", nullable = false)
  private String nombreLista;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 100)
  private Tipo tipo;

  @OneToMany(
      mappedBy = "lista",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ListaGameItemEntity> items = new ArrayList<>();

  public ListaGameEntity() {
    // vacio para jpa
  }
}
