package com.gamelist.catalogo.infrastructure.out.persistence.postgres.mapper;

import com.gamelist.catalogo.domain.syncstate.SyncKey;
import com.gamelist.catalogo.domain.syncstate.SyncState;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.SyncStateEntity;
import org.springframework.stereotype.Component;

@Component
public class SyncStateMapper {

  public SyncStateEntity toEntity(SyncState syncState) {
    if (syncState == null) {
      return null;
    }

    SyncStateEntity entity = new SyncStateEntity();
    entity.setKey(syncState.getKey().name());
    entity.setValue(syncState.getValue());
    entity.setUpdatedAt(syncState.getUpdatedAt());

    return entity;
  }

  public SyncState toDomain(SyncStateEntity entity) {
    if (entity == null) {
      return null;
    }

    SyncKey key = SyncKey.valueOf(entity.getKey());

    return SyncState.reconstitute(key, entity.getValue(), entity.getUpdatedAt());
  }

  public void updateEntity(SyncState syncState, SyncStateEntity entity) {
    entity.setValue(syncState.getValue());
    entity.setUpdatedAt(syncState.getUpdatedAt());
  }
}
