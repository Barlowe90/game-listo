package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatformEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "abbreviation")
  private String abbreviation;

  @Column(name = "alternativeName")
  private String alternativeName;

  @Column(name = "logoURL")
  private String logoURL;

  @Column(name = "tipo")
  private String tipo;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlatformEntity that)) return false;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
