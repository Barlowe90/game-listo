package com.gamelist.catalogo.infrastructure.in.igdb;

import org.springframework.stereotype.Component;

/** Constuctor querys para IGDB */
@Component
public class IgdbQueryBuilder {

  public String buildGamesQuery(Long afterId, int limit) {
    StringBuilder query = new StringBuilder();

    query.append("fields ");

    query.append(
        "id,"
            + "name,"
            + "summary,"
            + "cover.url,"
            + "platforms.name,"
            + "game_type.type,"
            + "alternative_names.name,"
            + "dlcs.id,"
            + "expanded_games.id,"
            + "expansions.id,"
            + "external_games.url,"
            + "franchises.name,"
            + "game_modes.name,"
            + "game_status.status,"
            + "genres.name,"
            + "involved_companies.company.name,"
            + "keywords.name,"
            + "multiplayer_modes.id,"
            + "parent_game.id,"
            + "player_perspectives.name,"
            + "remakes.id,"
            + "remasters.id,"
            + "similar_games.id,"
            + "themes.name,"
            + "screenshots.url,"
            + "videos.id;");

    if (afterId != null && afterId > 0) {
      query.append(" where id > ").append(afterId).append(";");
    }

    query.append(" limit ").append(Math.min(limit, 500)).append(";");
    query.append(" sort id asc;");

    return query.toString();
  }

  public String buildPlatformsQuery() {
    return "fields id,name,abbreviation,platform_logo,category; limit 500;";
  }
}
