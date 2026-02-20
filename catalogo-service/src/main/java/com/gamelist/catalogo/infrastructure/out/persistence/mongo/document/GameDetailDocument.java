package com.gamelist.catalogo.infrastructure.out.persistence.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "game_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDocument {

  @Id private String id; // MongoDB ObjectId

  private Long gameId; // ID del juego (referencia a PostgreSQL)

  private List<String> alternativeNames = new ArrayList<>();

  private String coverUrl;

  private List<String> screenshots = new ArrayList<>();

  private List<String> videos = new ArrayList<>();
}
