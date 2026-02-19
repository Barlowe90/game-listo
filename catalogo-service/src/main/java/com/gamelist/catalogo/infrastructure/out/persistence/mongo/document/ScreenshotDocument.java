package com.gamelist.catalogo.infrastructure.out.persistence.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotDocument {

  private String url;
  private Integer width;
  private Integer height;
}
