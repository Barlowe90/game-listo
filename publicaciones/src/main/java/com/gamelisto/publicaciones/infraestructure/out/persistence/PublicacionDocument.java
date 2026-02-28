package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.EstadoPublicacion;
import com.gamelisto.publicaciones.domain.EstiloJuego;
import com.gamelisto.publicaciones.domain.Experiencia;
import com.gamelisto.publicaciones.domain.Idioma;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "publicaciones")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionDocument {
  @Id private UUID id;

  @Indexed private UUID autorId;

  @Indexed private Long gameId;

  private String titulo;

  private Idioma idioma;

  private Experiencia experiencia;

  private EstiloJuego estiloJuego;

  private int jugadoresMaximos;

  @Indexed private EstadoPublicacion estadoPublicacion;
}
