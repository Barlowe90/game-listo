package com.gamelisto.graphql.controller;

import com.gamelisto.graphql.client.CatalogoClient;
import com.gamelisto.graphql.client.PublicacionesClient;
import com.gamelisto.graphql.model.Game;
import com.gamelisto.graphql.model.GameDetail;
import com.gamelisto.graphql.model.GameMedia;
import com.gamelisto.graphql.model.GrupoJuego;
import com.gamelisto.graphql.model.Publicacion;
import com.gamelisto.graphql.model.UsuarioRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
public class VideojuegoQueryController {
    private static final Logger log = LoggerFactory.getLogger(VideojuegoQueryController.class);

    private final CatalogoClient catalogoClient;
    private final PublicacionesClient publicacionesClient;

    public VideojuegoQueryController(CatalogoClient catalogoClient, PublicacionesClient publicacionesClient) {
        this.catalogoClient = catalogoClient;
        this.publicacionesClient = publicacionesClient;
    }

    @QueryMapping
    public GameDetail gameDetail(@Argument String id) {
        Integer gameId;
        try {
            gameId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.warn("ID de juego invalido recibido en gameDetail: {}", id);
            return null;
        }

        Game game = catalogoClient.getGameById(gameId);
        if (game == null) {
            return null;
        }

        Game safeGame = sanitizeGame(game, gameId);
        GameMedia media = sanitizeMedia(catalogoClient.getGameMedia(gameId));
        List<Publicacion> publicaciones = publicacionesClient.getPublicacionesByGame(id).stream()
                .map(this::sanitizePublicacion)
                .filter(Objects::nonNull)
                .toList();

        List<GrupoJuego> grupos = new ArrayList<>();
        for (Publicacion pub : publicaciones) {
            if (pub.grupoId() != null) {
                GrupoJuego grupo = sanitizeGrupo(publicacionesClient.getGrupoJuego(pub.grupoId()));
                if (grupo != null) {
                    grupos.add(grupo);
                }
            }
        }

        Set<Integer> relatedIdsToResolve = new HashSet<>();
        if (safeGame.parentGameId() != null) relatedIdsToResolve.add(safeGame.parentGameId());
        relatedIdsToResolve.addAll(safeIntegers(safeGame.dlcIds()));
        relatedIdsToResolve.addAll(safeIntegers(safeGame.expandedGames()));
        relatedIdsToResolve.addAll(safeIntegers(safeGame.expansionIds()));
        relatedIdsToResolve.addAll(safeIntegers(safeGame.remakeIds()));
        relatedIdsToResolve.addAll(safeIntegers(safeGame.remasterIds()));
        relatedIdsToResolve.addAll(safeIntegers(safeGame.similarGames()));

        List<Game> relatedGames = new ArrayList<>();
        for (Integer relId : relatedIdsToResolve) {
            Game relGame = catalogoClient.getGameById(relId);
            if (relGame != null) {
                relatedGames.add(sanitizeGame(relGame, relId));
            }
        }

        return new GameDetail(safeGame, media, relatedGames, publicaciones, grupos);
    }

    private Game sanitizeGame(Game game, Integer fallbackId) {
        return new Game(
                game.id() != null ? game.id() : fallbackId,
                nonNullString(game.name()),
                game.coverUrl(),
                game.summary(),
                safeStrings(game.alternativeNames()),
                safeIntegers(game.dlcIds()),
                safeIntegers(game.expandedGames()),
                safeIntegers(game.expansionIds()),
                safeStrings(game.externalGames()),
                safeStrings(game.franchises()),
                safeStrings(game.gameModes()),
                game.gameStatus(),
                game.gameType(),
                safeStrings(game.genres()),
                safeStrings(game.involvedCompanies()),
                safeStrings(game.keywords()),
                safeIntegers(game.multiplayerModeIds()),
                game.parentGameId(),
                safeStrings(game.platforms()),
                safeStrings(game.playerPerspectives()),
                safeIntegers(game.remakeIds()),
                safeIntegers(game.remasterIds()),
                safeIntegers(game.similarGames()),
                safeStrings(game.themes())
        );
    }

    private GameMedia sanitizeMedia(GameMedia media) {
        if (media == null) {
            return null;
        }
        return new GameMedia(media.gameId(), safeStrings(media.screenshots()), safeStrings(media.videos()));
    }

    private Publicacion sanitizePublicacion(Publicacion pub) {
        if (pub == null || pub.id() == null || pub.autorId() == null || pub.gameId() == null) {
            return null;
        }
        return new Publicacion(
                pub.id(),
                pub.autorId(),
                pub.gameId(),
                nonNullString(pub.titulo()),
                nonNullString(pub.idioma()),
                nonNullString(pub.experiencia()),
                nonNullString(pub.estiloJuego()),
                pub.jugadoresMaximos() != null ? pub.jugadoresMaximos() : 0,
                pub.grupoId(),
                pub.disponibilidad() != null ? pub.disponibilidad() : Map.of()
        );
    }

    private GrupoJuego sanitizeGrupo(GrupoJuego grupo) {
        if (grupo == null || grupo.id() == null || grupo.publicacionId() == null || grupo.fechaCreacion() == null) {
            return null;
        }

        List<UsuarioRef> participantes = grupo.participantes() == null
                ? List.of()
                : grupo.participantes().stream()
                .filter(Objects::nonNull)
                .filter(p -> p.id() != null && p.username() != null)
                .map(p -> new UsuarioRef(p.id(), p.username(), p.avatar()))
                .toList();

        return new GrupoJuego(grupo.id(), grupo.publicacionId(), grupo.fechaCreacion(), participantes);
    }

    private List<String> safeStrings(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().filter(Objects::nonNull).toList();
    }

    private List<Integer> safeIntegers(List<Integer> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().filter(Objects::nonNull).toList();
    }

    private String nonNullString(String value) {
        return value != null ? value : "";
    }
}
