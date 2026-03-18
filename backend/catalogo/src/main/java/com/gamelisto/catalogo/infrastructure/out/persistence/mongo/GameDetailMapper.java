package com.gamelisto.catalogo.infrastructure.out.persistence.mongo;

import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameDetailMapper {

  private static final int MAX_MEDIA_ITEMS = 40;

  public GameDetailDocument toDocument(GameDetail gameDetail) {
    if (gameDetail == null) {
      return null;
    }

    GameDetailDocument document = new GameDetailDocument();
    document.setGameId(gameDetail.getGameId().value());
    document.setScreenshots(sanitizeMedia(gameDetail.getScreenshots()));
    document.setVideos(sanitizeMedia(gameDetail.getVideos()));

    return document;
  }

  public GameDetail toDomain(GameDetailDocument document) {
    if (document == null) {
      return null;
    }

    GameId gameId = GameId.of(document.getGameId());
    List<String> screenshots = sanitizeMedia(document.getScreenshots());
    List<String> videos = sanitizeMedia(document.getVideos());

    return GameDetail.reconstitute(gameId, screenshots, videos);
  }

  private ArrayList<String> sanitizeMedia(List<String> media) {
    if (media == null) {
      return new ArrayList<>();
    }

    return media.stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .distinct()
        .limit(MAX_MEDIA_ITEMS)
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
