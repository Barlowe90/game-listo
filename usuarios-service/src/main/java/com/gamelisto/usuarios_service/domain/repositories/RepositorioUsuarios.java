package com.gamelisto.usuarios_service.domain.repositories;

import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Username;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface RepositorioUsuarios {

  Usuario save(Usuario usuario);

  Optional<Usuario> findById(UsuarioId id);

  Optional<Usuario> findByEmail(Email email);

  Optional<Usuario> findByUsername(Username username);

  Optional<Usuario> findByDiscordUserId(DiscordUserId discordUserId);

  Optional<Usuario> findByTokenVerificacion(TokenVerificacion token);

  List<Usuario> findByStatus(EstadoUsuario status);

  List<Usuario> findByStatusAndNotificationsActive(
      EstadoUsuario status, boolean notificationsActive);

  boolean existsByUsername(Username username);

  boolean existsByEmail(Email email);

  // List<Usuario> searchByUsernameFragment(String fragment); // Para
  // autocompletar en búsqueda de usuarios

  List<Usuario> findAll();

  void delete(Usuario usuario);
}
