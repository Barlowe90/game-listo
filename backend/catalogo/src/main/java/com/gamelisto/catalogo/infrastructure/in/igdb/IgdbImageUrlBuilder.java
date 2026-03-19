package com.gamelisto.catalogo.infrastructure.in.igdb;

public final class IgdbImageUrlBuilder {

  private static final String BASE_URL = "https://images.igdb.com/igdb/image/upload/";
  private static final String DEFAULT_EXTENSION = ".jpg";

  private IgdbImageUrlBuilder() {}

  public static String buildSizedUrl(String imageId, String rawUrl, String size) {
    if (imageId == null || imageId.isBlank() || size == null || size.isBlank()) {
      return normalizeUrl(rawUrl);
    }

    return BASE_URL + "t_" + size + "/" + imageId + extractExtension(rawUrl);
  }

  public static String normalizeUrl(String rawUrl) {
    if (rawUrl == null || rawUrl.isBlank()) {
      return null;
    }

    if (rawUrl.startsWith("//")) {
      return "https:" + rawUrl;
    }

    if (rawUrl.startsWith("http")) {
      return rawUrl;
    }

    return "https://" + rawUrl;
  }

  private static String extractExtension(String rawUrl) {
    String normalizedUrl = normalizeUrl(rawUrl);
    if (normalizedUrl == null) {
      return DEFAULT_EXTENSION;
    }

    int queryIndex = normalizedUrl.indexOf('?');
    String urlWithoutQuery = queryIndex >= 0 ? normalizedUrl.substring(0, queryIndex) : normalizedUrl;
    int lastSlash = urlWithoutQuery.lastIndexOf('/');
    int lastDot = urlWithoutQuery.lastIndexOf('.');

    if (lastDot > lastSlash) {
      return urlWithoutQuery.substring(lastDot);
    }

    return DEFAULT_EXTENSION;
  }
}
