package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity.UsuarioEntity;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    
    Optional<UsuarioEntity> findByEmail(String email);

    Optional<UsuarioEntity> findByUsername(String username);

    List<UsuarioEntity> findByStatus(EstadoUsuario status);

    @Query("SELECT u FROM UsuarioEntity u WHERE u.status = 'ACTIVO' AND u.notificationsActive = true")
    List<UsuarioEntity> findUsersWithNotificationsEnabled();
    
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM UsuarioEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :fragment, '%'))")
    List<UsuarioEntity> searchByUsernameFragment(@Param("fragment") String fragment);
}
