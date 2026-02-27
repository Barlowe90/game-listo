package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CambiarRolUsuarioUseCase {
  private final RepositorioUsuarios repositorioUsuarios;

  public CambiarRolUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(CambiarRolUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    usuario.changeRole(command.rol());
    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioActualizado);
  }
}
