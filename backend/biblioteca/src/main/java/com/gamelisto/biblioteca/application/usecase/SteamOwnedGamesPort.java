package com.gamelisto.biblioteca.application.usecase;

import java.util.List;

public interface SteamOwnedGamesPort {

  List<SteamOwnedGame> findOwnedGames(String steamId64);
}
