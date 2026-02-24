package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private final RepositorioLista repositorioLista;

  public CrearListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGameResult execute(CrearListaGameCommand command) {
    UUID usuarioRefId = UUID.fromString(command.usuarioRefId());
    NombreListaGame nombreListaGame = NombreListaGame.of(command.nombre());
    Tipo tipo = Tipo.valueOf(command.tipo());

    ListaGame listaGame = ListaGame.create(usuarioRefId, nombreListaGame, tipo);
    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }
}
