package com.gamelist.catalogo.infrastructure.igdb;

import org.springframework.stereotype.Component;

/**
 * Constructor de queries para la API de IGDB.
 *
 * <p>IGDB usa un lenguaje de query tipo Apicalypse (similar a SQL). Este builder genera las queries
 * en el formato esperado por IGDB.
 *
 * <p>Ejemplo de query:
 *
 * <pre>
 * fields id,name,summary,cover.url,platforms;
 * where id > 150000;
 * limit 500;
 * sort id asc;
 * </pre>
 */
@Component
public class IgdbQueryBuilder {

  public String buildGamesQuery(Long afterId, int limit) {
    StringBuilder query = new StringBuilder();

    query.append("fields id,name,summary,cover.url,platforms;");

    if (afterId != null && afterId > 0) {
      query.append("where id > ").append(afterId).append(";");
    }

    // hay que limitar los resultados porque IGDB tiene limitación
    query.append("limit ").append(Math.min(limit, 500)).append(";");

    query.append("sort id asc;");

    return query.toString();
  }

  public String buildPlatformsQuery() {
    return "fields id,name,abbreviation,platform_logo,category; limit 500;";
  }

  public String buildGameByIdQuery(Long gameId) {
    return "fields id,name,summary,cover.url,platforms; where id = " + gameId + ";";
  }
}
