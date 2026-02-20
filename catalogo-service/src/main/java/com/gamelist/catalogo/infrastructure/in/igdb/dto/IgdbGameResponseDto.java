package com.gamelist.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** DTO de respuesta de IGDB para un juego (campos básicos del batch) */
public record IgdbGameResponseDto(
    Long id,
    String name,
    String summary,
    IgdbCoverDto cover,
    List<IgdbNameDto> platforms,
    List<IgdbNameDto> genres,
    @JsonProperty("game_modes") List<IgdbNameDto> gameModes,
    @JsonProperty("player_perspectives") List<IgdbNameDto> playerPerspectives,
    List<IgdbNameDto> keywords,
    List<IgdbNameDto> themes,
    List<IgdbNameDto> franchises,
    @JsonProperty("alternative_names") List<IgdbNameDto> alternativeNames,
    @JsonProperty("involved_companies") List<IgdbInvolvedCompanyDto> involvedCompanies,
    @JsonProperty("external_games") List<IgdbExternalGameDto> externalGames,
    List<IgdbIdDto> dlcs,
    @JsonProperty("expanded_games") List<IgdbIdDto> expandedGames,
    List<IgdbIdDto> expansions,
    List<IgdbIdDto> remakes,
    List<IgdbIdDto> remasters,
    @JsonProperty("similar_games") List<IgdbIdDto> similarGames,
    @JsonProperty("multiplayer_modes") List<IgdbIdDto> multiplayerModes,
    @JsonProperty("parent_game") IgdbIdDto parentGame,
    @JsonProperty("game_type") IgdbGameTypeDto gameType,
    @JsonProperty("game_status") IgdbGameStatusDto gameStatus,
    List<IgdbScreenshotResponseDto> screenshots,
    List<IgdbVideoResponseDto> videos) {}
