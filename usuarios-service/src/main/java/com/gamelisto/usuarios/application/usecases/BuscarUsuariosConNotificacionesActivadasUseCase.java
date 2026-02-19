package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarUsuariosConNotificacionesActivadasUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public BuscarUsuariosConNotificacionesActivadasUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public List<UsuarioDTO> execute() {
    return repositorioUsuarios
        .findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true)
        .stream()
        .map(UsuarioDTO::from)
        .toList();
  }
}
