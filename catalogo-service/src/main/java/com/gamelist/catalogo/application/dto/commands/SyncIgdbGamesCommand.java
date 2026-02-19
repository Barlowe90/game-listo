package com.gamelist.catalogo.application.dto.commands;

public record SyncIgdbGamesCommand(Long fromId, Integer limit) {
  public SyncIgdbGamesCommand {
    if (limit == null || limit <= 0 || limit > 500) {
      limit = 500; // Default
    }
  }
}
