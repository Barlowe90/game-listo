package com.gamelist.catalogo_service.domain.exceptions;

import com.gamelist.catalogo_service.domain.syncstate.SyncKey;

public class SyncStateNotFoundException extends RuntimeException {

  public SyncStateNotFoundException(SyncKey key) {
    super("Estado de sincronización no encontrado: " + key);
  }

  public SyncStateNotFoundException(String message) {
    super(message);
  }
}
