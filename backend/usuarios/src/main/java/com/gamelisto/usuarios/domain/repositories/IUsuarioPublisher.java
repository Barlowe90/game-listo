package com.gamelisto.usuarios.domain.repositories;

import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;

public interface IUsuarioPublisher {

  void publicarUsuarioCreado(UsuarioCreado evento);

  void publicarUsuarioEliminado(UsuarioEliminado evento);
}
