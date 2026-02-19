package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.syncstate.SyncKey;
import com.gamelist.catalogo.domain.syncstate.SyncState;

import java.util.Optional;

public interface ISyncStateRepository {

  SyncState save(SyncState syncState);

  Optional<SyncState> findByKey(SyncKey key);

  void deleteByKey(SyncKey key);
}
