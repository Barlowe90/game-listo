package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CambiarRolUsuarioUseCase implements CambiarRolUsuarioHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional
  public UsuarioResult execute(CambiarRolUsuarioCommand command) {
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

    return UsuarioResult.from(usuarioActualizado);
  }
}
