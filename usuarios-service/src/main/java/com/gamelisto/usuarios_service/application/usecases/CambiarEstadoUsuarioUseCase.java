package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
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
            .orElseThrow(() -> new UsuarioNoEncontradoException(command.usuarioId()));

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
