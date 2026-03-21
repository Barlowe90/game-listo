package com.gamelisto.usuarios.domain.repositories;

import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;

public interface IUsuarioPublisher {

  void publicarUsuarioActualizado(UsuarioActualizado evento);

  void publicarUsuarioCreado(UsuarioCreado evento);

  void publicarUsuarioEliminado(UsuarioEliminado evento);
}
