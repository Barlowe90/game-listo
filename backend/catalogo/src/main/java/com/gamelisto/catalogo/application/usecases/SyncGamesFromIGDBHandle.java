package com.gamelisto.catalogo.application.usecases;

public interface SyncGamesFromIGDBHandle {
  SyncResultResult execute(int limit);
}
