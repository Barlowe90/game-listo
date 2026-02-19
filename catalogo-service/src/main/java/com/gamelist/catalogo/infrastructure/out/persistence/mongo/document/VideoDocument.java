package com.gamelist.catalogo.infrastructure.persistence.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoDocument {

  private String url;
  private String videoId;
}
