package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;

import java.util.List;

public interface BuscarUsuariosPorEstadoHandle {
  List<UsuarioResult> execute(EstadoUsuario estadoUsuario);
}
