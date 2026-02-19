package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.syncstate.SyncKey;
import com.gamelist.catalogo_service.domain.syncstate.SyncState;

import java.util.Optional;

public interface ISyncStateRepository {

  SyncState save(SyncState syncState);

  Optional<SyncState> findByKey(SyncKey key);

  void deleteByKey(SyncKey key);
}
