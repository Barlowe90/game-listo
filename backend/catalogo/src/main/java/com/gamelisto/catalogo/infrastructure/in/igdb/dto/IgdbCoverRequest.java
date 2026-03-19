package com.gamelisto.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gamelisto.catalogo.infrastructure.in.igdb.IgdbImageUrlBuilder;

public record IgdbCoverRequest(
    String url, @JsonProperty("image_id") String imageId, Integer width, Integer height) {

  public String getFullUrl() {
    return IgdbImageUrlBuilder.normalizeUrl(url);
  }

  public String toSizedUrl(String size) {
    return IgdbImageUrlBuilder.buildSizedUrl(imageId, url, size);
  }
}
