package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CambiarEstadoUsuarioUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public CambiarEstadoUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(CambiarEstadoUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    EstadoUsuario nuevoEstado = command.estadoUsuario();

    if (nuevoEstado == EstadoUsuario.SUSPENDIDO) {
      usuario.suspend();
    } else if (nuevoEstado == EstadoUsuario.ACTIVO) {
      usuario.activate();
    }

    Usuario usuarioEditado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioEditado);
  }
}
