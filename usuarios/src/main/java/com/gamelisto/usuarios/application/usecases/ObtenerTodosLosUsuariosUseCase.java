package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import java.util.List;
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
    return repositorioUsuarios.findAll().stream().map(UsuarioDTO::from).toList();
  }
}
