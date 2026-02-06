package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.syncstate.SyncKey;
import com.gamelist.catalogo_service.domain.syncstate.SyncState;

import java.util.Optional;

public interface ISyncStateRepository {

  Optional<SyncState> findByKey(SyncKey key);

  SyncState save(SyncState syncState);

  void deleteByKey(SyncKey key);
}
