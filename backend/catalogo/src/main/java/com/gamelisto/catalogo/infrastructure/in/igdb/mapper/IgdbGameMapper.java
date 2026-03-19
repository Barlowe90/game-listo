package com.gamelisto.catalogo.infrastructure.in.igdb.mapper;

import com.gamelisto.catalogo.application.usecases.IgdbGameDTO;
import com.gamelisto.catalogo.infrastructure.in.igdb.IgdbImageSizes;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IgdbGameMapper {

  public IgdbGameDTO toApplicationDto(GameFromIGDBResponse gameIGDBdto) {
    if (gameIGDBdto == null) return null;

    return new IgdbGameDTO(
        gameIGDBdto.id(),
        extractNames(gameIGDBdto.alternativeNames()),
        gameIGDBdto.cover() != null
            ? gameIGDBdto.cover().toSizedUrl(IgdbImageSizes.COVER_HIGH)
            : null,
        extractIds(gameIGDBdto.dlcs()),
        extractIds(gameIGDBdto.expandedGames()),
        extractIds(gameIGDBdto.expansions()),
        extractUrls(gameIGDBdto.externalGames()),
        extractNames(gameIGDBdto.franchises()),
        extractNames(gameIGDBdto.gameModes()),
        gameIGDBdto.gameStatus() != null ? gameIGDBdto.gameStatus().status() : null,
        gameIGDBdto.gameType() != null ? gameIGDBdto.gameType().type() : null,
        extractNames(gameIGDBdto.genres()),
        extractCompanies(gameIGDBdto.involvedCompanies()),
        extractNames(gameIGDBdto.keywords()),
        extractIds(gameIGDBdto.multiplayerModes()),
        gameIGDBdto.name(),
        gameIGDBdto.parentGame() != null ? gameIGDBdto.parentGame().id() : null,
        extractNames(gameIGDBdto.platforms()),
        extractNames(gameIGDBdto.playerPerspectives()),
        extractIds(gameIGDBdto.remakes()),
        extractIds(gameIGDBdto.remasters()),
        extractIds(gameIGDBdto.similarGames()),
        gameIGDBdto.summary(),
        extractNames(gameIGDBdto.themes()),
        extractScreenshotUrls(gameIGDBdto.screenshots()),
        extractVideoUrls(gameIGDBdto.videos()));
  }

  // Helpers
  private List<String> extractNames(List<IgdbNameRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream().map(IgdbNameRequest::name).toList();
  }

  private List<Long> extractIds(List<IgdbIdRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream().map(IgdbIdRequest::id).filter(java.util.Objects::nonNull).toList();
  }

  private List<String> extractUrls(List<IgdbExternalGameRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(IgdbExternalGameRequest::url)
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private List<String> extractCompanies(List<IgdbInvolvedCompanyRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(ic -> ic.company() != null ? ic.company().name() : null)
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private List<String> extractScreenshotUrls(List<IgdbScreenshotRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(screenshot -> screenshot.toSizedUrl(IgdbImageSizes.SCREENSHOT_HIGH))
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private List<String> extractVideoUrls(List<IgdbVideoRequest> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(IgdbVideoRequest::getYouTubeUrl)
        .filter(java.util.Objects::nonNull)
        .toList();
  }
}
