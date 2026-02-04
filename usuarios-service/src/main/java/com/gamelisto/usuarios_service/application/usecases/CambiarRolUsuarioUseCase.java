package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CambiarRolUsuarioUseCase {
  private static final Logger logger = LoggerFactory.getLogger(CambiarRolUsuarioUseCase.class);
  private final RepositorioUsuarios repositorioUsuarios;

  public CambiarRolUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(CambiarRolUsuarioCommand command) {
    logger.info("Cambiando rol de usuario {} a {}", command.usuarioId(), command.rol());
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());
    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(() -> new UsuarioNoEncontradoException(command.usuarioId()));
    usuario.changeRole(command.rol());
    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);
    logger.info(
        "Rol de usuario {} cambiado exitosamente a {}", command.usuarioId(), command.rol());
    return UsuarioDTO.from(usuarioActualizado);
  }
}
