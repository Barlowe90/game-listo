package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarUsuariosPorEstadoUseCase implements BuscarUsuariosPorEstadoHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional(readOnly = true)
  public List<UsuarioResult> execute(EstadoUsuario estadoUsuario) {
    return repositorioUsuarios.findByStatus(estadoUsuario).stream()
        .map(UsuarioResult::from)
        .toList();
  }
}
