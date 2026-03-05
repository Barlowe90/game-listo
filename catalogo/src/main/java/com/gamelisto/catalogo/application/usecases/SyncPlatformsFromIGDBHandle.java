package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.out.SyncResultDTO;

public interface SyncPlatformsFromIGDBHandle {
  SyncResultDTO execute();
}
