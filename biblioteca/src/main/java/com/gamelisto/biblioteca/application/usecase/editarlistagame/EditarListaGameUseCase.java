package com.gamelisto.biblioteca.application.usecase.editarlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarListaGameUseCase implements EditarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public EditarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public EditarListaGameResult execute(EditarListaGameCommand command) {
    NombreListaGame nuevoNombreListaGame = NombreListaGame.of(command.nombre());
    UUID uuidLista = UUID.fromString(command.idLista());
    ListaGameId idLista = ListaGameId.of(uuidLista);

    ListaGame listaGame =
        repositorioLista
            .findById(idLista)
            .orElseThrow(() -> new ApplicationException("No se encuentra la lista " + idLista));

    listaGame.cambiarNombre(nuevoNombreListaGame);
    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return EditarListaGameResult.from(listaGuardada);
  }
}
