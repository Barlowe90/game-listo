package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearBibliotecaParaUsuarioUseCase implements CrearBibliotecaParaUsuarioHandle {

  private final ListaGameRepositorio listaGameRepositorio;

  @Override
  public void execute(UUID userId) {
    guardarListaOficial(userId, Estado.DESEADO);
    guardarListaOficial(userId, Estado.PENDIENTE);
    guardarListaOficial(userId, Estado.JUGANDO);
    guardarListaOficial(userId, Estado.COMPLETADO);
    guardarListaOficial(userId, Estado.ABANDONADO);
  }

  private void guardarListaOficial(UUID userId, Estado estado) {
    ListaGame lista =
        ListaGame.create(UsuarioId.of(userId), NombreListaGame.of(estado.toString()), Tipo.OFICIAL);
    listaGameRepositorio.save(lista);
  }
}
