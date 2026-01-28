package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarUsuariosPorEstadoUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public BuscarUsuariosPorEstadoUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public List<UsuarioDTO> execute(EstadoUsuario estadoUsuario) {
    return repositorioUsuarios.findByStatus(estadoUsuario).stream()
        .map(UsuarioDTO::from)
        .collect(Collectors.toList());
  }
}
