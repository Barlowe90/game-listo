package com.gamelist.catalogo_service.infrastructure.persistence.mongo.mapper;

import com.gamelist.catalogo_service.domain.game.GameId;
import com.gamelist.catalogo_service.domain.gamedetail.GameDetail;
import com.gamelist.catalogo_service.domain.gamedetail.Screenshot;
import com.gamelist.catalogo_service.domain.gamedetail.Video;
import com.gamelist.catalogo_service.infrastructure.persistence.mongo.document.GameDetailDocument;
import com.gamelist.catalogo_service.infrastructure.persistence.mongo.document.ScreenshotDocument;
import com.gamelist.catalogo_service.infrastructure.persistence.mongo.document.VideoDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameDetailMapper {

  public GameDetailDocument toDocument(GameDetail gameDetail) {
    if (gameDetail == null) {
      return null;
    }

    GameDetailDocument document = new GameDetailDocument();
    document.setGameId(gameDetail.getGameId().value());

    List<ScreenshotDocument> screenshotDocs =
        gameDetail.getScreenshots().stream().map(this::toScreenshotDocument).toList();
    document.setScreenshots(screenshotDocs);

    List<VideoDocument> videoDocs =
        gameDetail.getVideos().stream().map(this::toVideoDocument).toList();
    document.setVideos(videoDocs);

    return document;
  }

  public GameDetail toDomain(GameDetailDocument document) {
    if (document == null) {
      return null;
    }

    GameId gameId = GameId.of(document.getGameId());

    List<Screenshot> screenshots =
        document.getScreenshots().stream().map(this::toScreenshot).toList();

    List<Video> videos = document.getVideos().stream().map(this::toVideo).toList();

    return GameDetail.reconstitute(gameId, screenshots, videos);
  }

  private ScreenshotDocument toScreenshotDocument(Screenshot screenshot) {
    return new ScreenshotDocument(screenshot.url(), screenshot.width(), screenshot.height());
  }

  private Screenshot toScreenshot(ScreenshotDocument doc) {
    return Screenshot.of(doc.getUrl(), doc.getWidth(), doc.getHeight());
  }

  private VideoDocument toVideoDocument(Video video) {
    return new VideoDocument(video.url(), video.videoId());
  }

  private Video toVideo(VideoDocument doc) {
    return Video.of(doc.getUrl(), doc.getVideoId());
  }
}
