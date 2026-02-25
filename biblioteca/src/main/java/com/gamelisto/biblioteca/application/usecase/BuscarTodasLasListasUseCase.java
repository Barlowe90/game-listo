package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.ListaGameRepositorio;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarTodasLasListasUseCase implements BuscarTodasLasListasGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  public BuscarTodasLasListasUseCase(ListaGameRepositorio listaGameRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
  }

  @Transactional
  public List<ListaGameResult> execute(String userId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId);

    return listaGameRepositorio.findByUsuarioRefId(result.userUuid).stream()
        .map(ListaGameResult::from)
        .toList();
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String userId) {
    UUID userUuid = UUID.fromString(userId);
    return new EntradaBuscarListaGame(userUuid);
  }

  private record EntradaBuscarListaGame(UUID userUuid) {}
}
