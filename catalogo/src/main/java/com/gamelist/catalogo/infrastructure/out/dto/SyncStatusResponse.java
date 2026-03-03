package com.gamelist.catalogo.infrastructure.out.dto;

import com.gamelist.catalogo.application.dto.out.SyncResultDTO;

public record SyncStatusResponse(int totalSynced, Long lastId, String message) {

  public static SyncStatusResponse from(SyncResultDTO result, String message) {
    return new SyncStatusResponse(result.totalSynced(), result.lastId(), message);
  }
}
