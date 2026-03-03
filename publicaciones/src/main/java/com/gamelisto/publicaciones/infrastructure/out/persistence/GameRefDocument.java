package com.gamelisto.publicaciones.infrastructure.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "games_ref")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameRefDocument {

  @Id private Long id;

  private String nombre;

  private String plataforma;
}
