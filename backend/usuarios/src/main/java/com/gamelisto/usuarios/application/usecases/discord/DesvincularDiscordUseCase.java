package com.gamelisto.usuarios.application.usecases.discord;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesvincularDiscordUseCase implements DesvincularDiscordHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional
  public UsuarioResult execute(UUID usuarioId) {

    UsuarioId id = UsuarioId.of(usuarioId);
    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con ID: " + usuarioId));

    usuario.unlinkDiscord();

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioResult.from(usuarioActualizado);
  }
}
