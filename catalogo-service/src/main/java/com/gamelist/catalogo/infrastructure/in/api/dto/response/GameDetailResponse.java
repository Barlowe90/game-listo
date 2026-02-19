package com.gamelist.catalogo.infrastructure.api.dto.response;

import com.gamelist.catalogo.application.dto.results.GameDetailDTO;
import com.gamelist.catalogo.application.usecases.GetGameDetailUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Response DTO para detalles completos de un juego (básico + multimedia). */
@Schema(description = "Detalles completos de un videojuego incluyendo multimedia")
public record GameDetailResponse(
    @Schema(description = "Información básica del juego") GameResponse game,
    @Schema(description = "Screenshots del juego") List<ScreenshotResponse> screenshots,
    @Schema(description = "Videos del juego") List<VideoResponse> videos) {

  public static GameDetailResponse from(GetGameDetailUseCase.GameWithDetailDTO dto) {
    GameResponse gameResponse = GameResponse.from(dto.game());

    List<ScreenshotResponse> screenshotResponses =
        dto.detail().screenshots().stream().map(ScreenshotResponse::from).toList();

    List<VideoResponse> videoResponses =
        dto.detail().videos().stream().map(VideoResponse::from).toList();

    return new GameDetailResponse(gameResponse, screenshotResponses, videoResponses);
  }

  @Schema(description = "Screenshot de un juego")
  public record ScreenshotResponse(
      @Schema(description = "URL del screenshot") String url,
      @Schema(description = "Ancho en píxeles") Integer width,
      @Schema(description = "Alto en píxeles") Integer height) {

    public static ScreenshotResponse from(GameDetailDTO.ScreenshotDTO dto) {
      return new ScreenshotResponse(dto.url(), dto.width(), dto.height());
    }
  }

  @Schema(description = "Video de un juego")
  public record VideoResponse(
      @Schema(description = "ID del video") String videoId,
      @Schema(description = "Nombre del video") String name) {

    public static VideoResponse from(GameDetailDTO.VideoDTO dto) {
      return new VideoResponse(dto.videoId(), dto.name());
    }
  }
}
