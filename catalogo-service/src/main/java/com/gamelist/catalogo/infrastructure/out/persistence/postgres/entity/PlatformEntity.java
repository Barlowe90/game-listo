package com.gamelist.catalogo.infrastructure.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatformEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id; // ID de IGDB

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "abbreviation", length = 20)
  private String abbreviation;

  @ManyToMany(mappedBy = "platforms", fetch = FetchType.LAZY)
  private Set<GameEntity> games = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlatformEntity)) return false;
    PlatformEntity that = (PlatformEntity) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
