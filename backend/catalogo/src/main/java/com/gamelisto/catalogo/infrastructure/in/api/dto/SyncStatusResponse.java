package com.gamelisto.catalogo.infrastructure.in.api.dto;

import com.gamelisto.catalogo.application.usecases.SyncResultResult;

public record SyncStatusResponse(int totalSynced, Long lastId, String message) {

  public static SyncStatusResponse from(SyncResultResult result, String message) {
    return new SyncStatusResponse(result.totalSynced(), result.lastId(), message);
  }
}
