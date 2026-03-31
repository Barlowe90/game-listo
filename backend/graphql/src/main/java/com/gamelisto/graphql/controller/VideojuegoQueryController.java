package com.gamelisto.graphql.controller;
import com.gamelisto.graphql.client.CatalogoClient;
import com.gamelisto.graphql.client.PublicacionesClient;
import com.gamelisto.graphql.model.Game;
import com.gamelisto.graphql.model.GameDetail;
import com.gamelisto.graphql.model.GameMedia;
import com.gamelisto.graphql.model.GrupoJuego;
import com.gamelisto.graphql.model.Publicacion;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Controller
public class VideojuegoQueryController {
    private final CatalogoClient catalogoClient;
    private final PublicacionesClient publicacionesClient;
    public VideojuegoQueryController(CatalogoClient catalogoClient, PublicacionesClient publicacionesClient) {
        this.catalogoClient = catalogoClient;
        this.publicacionesClient = publicacionesClient;
    }
    @QueryMapping
    public GameDetail gameDetail(@Argument String id) {
        Integer gameId = Integer.parseInt(id);
        Game game = catalogoClient.getGameById(gameId);
        if (game == null) {
            return null; // Or throw exception based on error handling
        }
        GameMedia media = catalogoClient.getGameMedia(gameId);
        List<Publicacion> publicaciones = publicacionesClient.getPublicacionesByGame(id);
        List<GrupoJuego> grupos = new ArrayList<>();
        for (Publicacion pub : publicaciones) {
            if (pub.grupoId() != null) {
                GrupoJuego grupo = publicacionesClient.getGrupoJuego(pub.grupoId());
                if (grupo != null) {
                    grupos.add(grupo);
                }
            }
        }
        Set<Integer> relatedIdsToResolve = new HashSet<>();
        if (game.parentGameId() != null) relatedIdsToResolve.add(game.parentGameId());
        if (game.dlcIds() != null) relatedIdsToResolve.addAll(game.dlcIds());
        if (game.expandedGames() != null) relatedIdsToResolve.addAll(game.expandedGames());
        if (game.expansionIds() != null) relatedIdsToResolve.addAll(game.expansionIds());
        if (game.remakeIds() != null) relatedIdsToResolve.addAll(game.remakeIds());
        if (game.remasterIds() != null) relatedIdsToResolve.addAll(game.remasterIds());
        if (game.similarGames() != null) relatedIdsToResolve.addAll(game.similarGames());
        List<Game> relatedGames = new ArrayList<>();
        for (Integer relId : relatedIdsToResolve) {
            Game relGame = catalogoClient.getGameById(relId);
            if (relGame != null) {
                relatedGames.add(relGame);
            }
        }
        return new GameDetail(game, media, relatedGames, publicaciones, grupos);
    }
}
