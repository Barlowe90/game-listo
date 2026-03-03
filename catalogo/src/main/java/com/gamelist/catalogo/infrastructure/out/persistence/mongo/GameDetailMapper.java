package com.gamelist.catalogo.infrastructure.out.persistence.mongo;

import com.gamelist.catalogo.domain.GameId;
import com.gamelist.catalogo.domain.GameDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameDetailMapper {

  public GameDetailDocument toDocument(GameDetail gameDetail) {
    if (gameDetail == null) {
      return null;
    }

    GameDetailDocument document = new GameDetailDocument();
    document.setGameId(gameDetail.getGameId().value());
    document.setScreenshots(
        gameDetail.getScreenshots() != null
            ? new ArrayList<>(gameDetail.getScreenshots())
            : new ArrayList<>());
    document.setVideos(
        gameDetail.getVideos() != null
            ? new ArrayList<>(gameDetail.getVideos())
            : new ArrayList<>());

    return document;
  }

  public GameDetail toDomain(GameDetailDocument document) {
    if (document == null) {
      return null;
    }

    GameId gameId = GameId.of(document.getGameId());

    List<String> screenshots =
        document.getScreenshots() != null
            ? new ArrayList<>(document.getScreenshots())
            : new ArrayList<>();
    List<String> videos =
        document.getVideos() != null ? new ArrayList<>(document.getVideos()) : new ArrayList<>();

    return GameDetail.reconstitute(gameId, screenshots, videos);
  }
}
