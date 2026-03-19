package com.gamelisto.catalogo.infrastructure.in.igdb;

import org.springframework.stereotype.Component;

/** Constuctor querys para IGDB */
@Component
public class IgdbQueryBuilder {

  private static final int LIMITE = 500;

  public String buildGamesQuery(Long afterId, int limit) {

    long start = (afterId == null || afterId <= 0) ? 0 : afterId;

    return "fields "
        + "alternative_names.name,"
        + "cover.image_id,"
        + "cover.url,"
        + "dlcs.id,"
        + "expanded_games.id,"
        + "expansions.id,"
        + "external_games.url,"
        + "franchises.name,"
        + "game_modes.name,"
        + "game_status.status,"
        + "game_type.type,"
        + "genres.name,"
        + "involved_companies.company.name,"
        + "keywords.name,"
        + "multiplayer_modes.*,"
        + "name,"
        + "parent_game.id,"
        + "platforms.name,"
        + "player_perspectives.name,"
        + "remakes.id,"
        + "remasters.id,"
        + "screenshots.image_id,"
        + "screenshots.url,"
        + "similar_games.id,"
        + "summary,"
        + "themes.name,"
        + "videos.video_id,"
        + "id"
        + ";"
        + " where id > "
        + start
        + ";"
        + " sort id asc;"
        + " limit "
        + Math.min(limit, 500)
        + ";";
  }

  public String buildPlatformsQuery() {

    return "fields "
        + "abbreviation,"
        + "alternative_name,"
        + "name,"
        + "platform_logo.image_id,"
        + "platform_logo.url,"
        + "platform_type.name,"
        + "id"
        + ";"
        + " limit "
        + LIMITE
        + ";";
  }
}
