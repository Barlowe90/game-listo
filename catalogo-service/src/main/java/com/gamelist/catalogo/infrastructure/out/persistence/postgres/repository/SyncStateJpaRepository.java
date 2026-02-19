package com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository;

import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.SyncStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncStateJpaRepository extends JpaRepository<SyncStateEntity, String> {}
