package com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id; // ID de IGDB

  @Column(name = "name", nullable = false, length = 200)
  private String name;

  @Column(name = "summary", length = 1000)
  private String summary;

  @Column(name = "cover_url", length = 500)
  private String coverUrl;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "game_platform",
      joinColumns = @JoinColumn(name = "game_id"),
      inverseJoinColumns = @JoinColumn(name = "platform_id"))
  private Set<PlatformEntity> platforms = new HashSet<>();
}
