package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerTodosLosUsuariosUseCase implements ObtenerTodosLosUsuariosHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional(readOnly = true)
  public List<UsuarioResult> execute() {
    return repositorioUsuarios.findAll().stream().map(UsuarioResult::from).toList();
  }
}
