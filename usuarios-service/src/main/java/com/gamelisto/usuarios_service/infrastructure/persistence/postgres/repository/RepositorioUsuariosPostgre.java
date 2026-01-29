package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.repository;

import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Username;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity.UsuarioEntity;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.mapper.UsuarioMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioUsuariosPostgre implements RepositorioUsuarios {

  private final UsuarioJpaRepository jpaRepository;
  private final UsuarioMapper mapper;

  public RepositorioUsuariosPostgre(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @SuppressWarnings("null")
  @Override
  public Usuario save(Usuario usuario) {
    UsuarioEntity entity = mapper.toEntity(usuario);
    UsuarioEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @SuppressWarnings("null")
  @Override
  public Optional<Usuario> findById(UsuarioId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Usuario> findByEmail(Email email) {
    return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Usuario> findByUsername(Username username) {
    return jpaRepository.findByUsername(username.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Usuario> findByDiscordUserId(DiscordUserId discordUserId) {
    if (discordUserId == null || discordUserId.isEmpty()) {
      return Optional.empty();
    }
    return jpaRepository.findByDiscordUserId(discordUserId.value()).map(mapper::toDomain);
  }

  @Override
  public List<Usuario> findByStatus(EstadoUsuario status) {
    return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Usuario> findByStatusAndNotificationsActive(
      EstadoUsuario status, boolean notificationsActive) {
    return jpaRepository.findByStatusAndNotificationsActive(status, notificationsActive).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<Usuario> findByTokenVerificacion(TokenVerificacion token) {
    if (token == null || token.isEmpty()) {
      return Optional.empty();
    }
    return jpaRepository.findByTokenVerificacion(token.value()).map(mapper::toDomain);
  }

  @Override
  public boolean existsByUsername(Username username) {
    return jpaRepository.existsByUsername(username.value());
  }

  @Override
  public boolean existsByEmail(Email email) {
    return jpaRepository.existsByEmail(email.value());
  }

  @Override
  public List<Usuario> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @SuppressWarnings("null")
  @Override
  public void delete(Usuario usuario) {
    UsuarioEntity entity = mapper.toEntity(usuario);
    jpaRepository.delete(entity);
  }
}
