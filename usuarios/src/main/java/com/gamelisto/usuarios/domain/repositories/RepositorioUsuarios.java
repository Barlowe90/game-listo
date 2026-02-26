package com.gamelisto.usuarios.domain.repositories;

import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios.domain.usuario.Username;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface RepositorioUsuarios {

  Usuario save(Usuario usuario);

  Optional<Usuario> findById(UsuarioId id);

  Optional<Usuario> findByEmail(Email email);

  Optional<Usuario> findByUsername(Username username);

  Optional<Usuario> findByDiscordUserId(DiscordUserId discordUserId);

  List<Usuario> findByStatus(EstadoUsuario status);

  Optional<Usuario> findByTokenVerificacion(TokenVerificacion token);

  boolean existsByUsername(Username username);

  boolean existsByEmail(Email email);

  List<Usuario> findAll();

  void delete(Usuario usuario);
}
