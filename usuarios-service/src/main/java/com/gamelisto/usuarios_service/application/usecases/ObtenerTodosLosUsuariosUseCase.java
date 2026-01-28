package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerTodosLosUsuariosUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public ObtenerTodosLosUsuariosUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public List<UsuarioDTO> execute() {
    return repositorioUsuarios.findAll().stream()
        .map(UsuarioDTO::from)
        .collect(Collectors.toList());
  }
}
