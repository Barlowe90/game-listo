package com.gamelisto.publicaciones.infrastructure.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "grupos_juegos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoJuegoDocument {

  @Id private UUID id;

  @Indexed(unique = true)
  private UUID publicacionId;

  private Instant fechaCreacion;
}
