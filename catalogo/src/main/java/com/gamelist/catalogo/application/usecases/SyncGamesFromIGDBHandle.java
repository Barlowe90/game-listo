package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.SyncResultDTO;

public interface SyncGamesFromIGDBHandle {
  SyncResultDTO execute(int limit);
}
