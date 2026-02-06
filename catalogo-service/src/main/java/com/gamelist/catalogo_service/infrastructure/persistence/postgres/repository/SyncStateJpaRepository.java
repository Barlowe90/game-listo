package com.gamelist.catalogo_service.infrastructure.persistence.postgres.repository;

import com.gamelist.catalogo_service.infrastructure.persistence.postgres.entity.SyncStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncStateJpaRepository extends JpaRepository<SyncStateEntity, String> {}
