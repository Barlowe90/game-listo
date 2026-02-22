package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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

  @Column(name = "avatar", length = 500)
  private String avatar;

  @Column(name = "rol")
  private String rol;

  public UsuarioRefEntity() {
    // constructor vacio requerido por jpa para instanciacion via reflection
  }
}
