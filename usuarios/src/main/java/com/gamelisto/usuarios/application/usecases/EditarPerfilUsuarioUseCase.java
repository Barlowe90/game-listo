package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditarPerfilUsuarioUseCase implements EditarPerfilUsuarioHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional
  public UsuarioResult execute(EditarPerfilUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.of(command.usuarioId());

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    if (command.avatar() != null) {
      usuario.changeAvatar(Avatar.of(command.avatar()));
    }

    if (command.language() != null) {
      try {
        usuario.changeLanguage(Idioma.valueOf(command.language()));
      } catch (IllegalArgumentException e) {
        throw new ApplicationException("Idioma inválido: " + command.language());
      }
    }

    Usuario usuarioEditado = repositorioUsuarios.save(usuario);

    return UsuarioResult.from(usuarioEditado);
  }
}
