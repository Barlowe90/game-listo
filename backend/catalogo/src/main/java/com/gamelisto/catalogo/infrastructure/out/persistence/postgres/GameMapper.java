package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import com.gamelisto.catalogo.domain.*;
import com.gamelisto.catalogo.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class GameMapper {

  public GameEntity toEntity(Game game) {
    GameEntity entity = new GameEntity();
    entity.setId(game.getId().value());
    entity.setName(game.getName().value());
    entity.setSummary(
        game.getSummary() != null && !game.getSummary().isEmpty()
            ? game.getSummary().value()
            : null);
    entity.setCoverUrl(
        game.getCoverUrl() != null && !game.getCoverUrl().isEmpty()
            ? game.getCoverUrl().value()
            : null);
    entity.setPlatforms(game.getPlatforms() != null ? game.getPlatforms() : new ArrayList<>());
    entity.setGameType(game.getGameType());
    entity.setGameStatus(game.getGameStatus());
    entity.setAlternativeNames(
        game.getAlternativeNames() != null ? game.getAlternativeNames() : new ArrayList<>());
    entity.setDlcIds(game.getDlcs() != null ? game.getDlcs() : new ArrayList<>());
    entity.setExpandedGames(
        game.getExpandedGames() != null ? game.getExpandedGames() : new ArrayList<>());
    entity.setExpansionIds(
        game.getExpansionIds() != null ? game.getExpansionIds() : new ArrayList<>());
    entity.setExternalGames(
        game.getExternalGames() != null ? game.getExternalGames() : new ArrayList<>());
    entity.setFranchises(game.getFranchises() != null ? game.getFranchises() : new ArrayList<>());
    entity.setGameModes(game.getGameModes() != null ? game.getGameModes() : new ArrayList<>());
    entity.setGenres(game.getGenres() != null ? game.getGenres() : new ArrayList<>());
    entity.setInvolvedCompanies(
        game.getInvolvedCompanies() != null ? game.getInvolvedCompanies() : new ArrayList<>());
    entity.setKeywords(game.getKeywords() != null ? game.getKeywords() : new ArrayList<>());
    entity.setMultiplayerModeIds(
        game.getMultiplayerModeIds() != null ? game.getMultiplayerModeIds() : new ArrayList<>());
    entity.setParentGameId(game.getParentGameId());
    entity.setPlayerPerspectives(
        game.getPlayerPerspectives() != null ? game.getPlayerPerspectives() : new ArrayList<>());
    entity.setRemakeIds(game.getRemakeIds() != null ? game.getRemakeIds() : new ArrayList<>());
    entity.setRemasterIds(
        game.getRemasterIds() != null ? game.getRemasterIds() : new ArrayList<>());
    entity.setSimilarGames(
        game.getSimilarGames() != null ? game.getSimilarGames() : new ArrayList<>());
    entity.setThemes(game.getThemes() != null ? game.getThemes() : new ArrayList<>());
    return entity;
  }

  public Game toDomain(GameEntity entity) {
    return Game.reconstitute(
        GameId.of(entity.getId()),
        GameName.of(entity.getName()),
        entity.getSummary() != null ? Summary.of(entity.getSummary()) : Summary.empty(),
        entity.getCoverUrl() != null ? CoverUrl.of(entity.getCoverUrl()) : CoverUrl.empty(),
        entity.getPlatforms(),
        entity.getGameType(),
        entity.getGameStatus(),
        entity.getAlternativeNames(),
        entity.getDlcIds(),
        entity.getExpandedGames(),
        entity.getExpansionIds(),
        entity.getExternalGames(),
        entity.getFranchises(),
        entity.getGameModes(),
        entity.getGenres(),
        entity.getInvolvedCompanies(),
        entity.getKeywords(),
        entity.getMultiplayerModeIds(),
        entity.getParentGameId(),
        entity.getPlayerPerspectives(),
        entity.getRemakeIds(),
        entity.getRemasterIds(),
        entity.getSimilarGames(),
        entity.getThemes());
  }
}
