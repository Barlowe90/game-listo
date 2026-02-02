package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPerfilAutenticadoUseCase {

  private static final Logger logger =
      LoggerFactory.getLogger(ObtenerPerfilAutenticadoUseCase.class);

  private final RepositorioUsuarios repositorioUsuarios;

  public ObtenerPerfilAutenticadoUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public UsuarioDTO execute(String userIdString) {
    logger.debug("Obteniendo perfil autenticado para userId: {}", userIdString);

    UsuarioId usuarioId = UsuarioId.of(UUID.fromString(userIdString));

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userIdString));

    return UsuarioDTO.from(usuario);
  }
}
