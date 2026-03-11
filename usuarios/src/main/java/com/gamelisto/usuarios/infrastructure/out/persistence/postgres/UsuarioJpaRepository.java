package com.gamelisto.usuarios.infrastructure.out.persistence.postgres;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {

  Optional<UsuarioEntity> findByEmail(String email);

  Optional<UsuarioEntity> findByUsername(String username);

  Optional<UsuarioEntity> findByDiscordUserId(String discordUserId);

  List<UsuarioEntity> findByStatus(EstadoUsuario status);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<UsuarioEntity> findByTokenVerificacion(String tokenVerificacion);
}
