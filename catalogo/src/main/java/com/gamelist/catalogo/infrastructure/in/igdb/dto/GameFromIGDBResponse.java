package com.gamelist.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** DTO de respuesta de IGDB para un juego */
public record GameFromIGDBResponse(
    @JsonProperty("alternative_names") List<IgdbNameRequest> alternativeNames,
    IgdbCoverRequest cover,
    List<IgdbIdRequest> dlcs,
    @JsonProperty("expanded_games") List<IgdbIdRequest> expandedGames,
    List<IgdbIdRequest> expansions,
    @JsonProperty("external_games") List<IgdbExternalGameRequest> externalGames,
    List<IgdbNameRequest> franchises,
    @JsonProperty("game_modes") List<IgdbNameRequest> gameModes,
    @JsonProperty("game_status") IgdbGameStatusRequest gameStatus,
    @JsonProperty("game_type") IgdbGameTypeRequest gameType,
    List<IgdbNameRequest> genres,
    @JsonProperty("involved_companies") List<IgdbInvolvedCompanyRequest> involvedCompanies,
    List<IgdbNameRequest> keywords,
    @JsonProperty("multiplayer_modes") List<IgdbIdRequest> multiplayerModes,
    String name,
    @JsonProperty("parent_game") IgdbIdRequest parentGame,
    List<IgdbNameRequest> platforms,
    @JsonProperty("player_perspectives") List<IgdbNameRequest> playerPerspectives,
    List<IgdbIdRequest> remakes,
    List<IgdbIdRequest> remasters,
    List<IgdbScreenshotRequest> screenshots,
    @JsonProperty("similar_games") List<IgdbIdRequest> similarGames,
    String summary,
    List<IgdbNameRequest> themes,
    List<IgdbVideoRequest> videos,
    Long id) {}
