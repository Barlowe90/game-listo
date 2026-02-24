package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private RepositorioLista repositorioLista;

  public CrearListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGame execute(CrearListaGameCommand command) {
    ListaGame listaGame =
        ListaGame.create(command.usuarioRefId(), command.nombre(), command.tipo());
    ListaGame listaGuardada = repositorioLista.save(listaGame);
    return CrearListaGameResult.from(listaGuardada);
  }
}
