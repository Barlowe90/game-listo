package com.gamelist.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** DTO de respuesta de IGDB para un juego completo con multimedia */
public record IgdbGameDetailResponseDto(
    Long id,
    String name,
    String summary,
    IgdbCoverDto cover,
    List<IgdbNameDto> platforms, // platforms.name expandido

    // Campos expandidos
    @JsonProperty("game_type") IgdbGameTypeDto gameType,
    @JsonProperty("alternative_names") List<IgdbNameDto> alternativeNames,
    List<IgdbIdDto> dlcs,
    @JsonProperty("expanded_games") List<IgdbIdDto> expandedGames,
    List<IgdbIdDto> expansions,
    @JsonProperty("external_games") List<IgdbExternalGameDto> externalGames,
    List<IgdbNameDto> franchises,
    @JsonProperty("game_modes") List<IgdbNameDto> gameModes,
    @JsonProperty("game_status") IgdbGameStatusDto gameStatus,
    List<IgdbNameDto> genres,
    @JsonProperty("involved_companies") List<IgdbInvolvedCompanyDto> involvedCompanies,
    List<IgdbNameDto> keywords,
    @JsonProperty("multiplayer_modes") List<IgdbIdDto> multiplayerModes,
    @JsonProperty("parent_game") IgdbIdDto parentGame,
    @JsonProperty("player_perspectives") List<IgdbNameDto> playerPerspectives,
    List<IgdbIdDto> remakes,
    List<IgdbIdDto> remasters,
    @JsonProperty("similar_games") List<IgdbIdDto> similarGames,
    List<IgdbNameDto> themes,

    // Multimedia
    List<IgdbScreenshotResponseDto> screenshots,
    List<IgdbVideoResponseDto> videos) {}
