package com.gamelist.catalogo.domain.syncstate;

public enum SyncKey {
  LAST_SYNCED_GAME_ID, // último día de juego sincronizado

  LAST_SYNC_TIMESTAMP, // marca temporal de la última sync

  TOTAL_GAMES_SYNCED // contador juegos sync de IGDB
}
