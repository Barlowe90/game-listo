package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarPerfilUsuarioUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public EditarPerfilUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(EditarPerfilUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

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
      usuario.changeLanguage(Idioma.valueOf(command.language()));
    }

    Usuario usuarioEditado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioEditado);
  }
}
