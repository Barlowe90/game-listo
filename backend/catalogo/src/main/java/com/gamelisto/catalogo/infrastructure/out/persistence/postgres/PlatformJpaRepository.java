package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformJpaRepository extends JpaRepository<PlatformEntity, Long> {}
