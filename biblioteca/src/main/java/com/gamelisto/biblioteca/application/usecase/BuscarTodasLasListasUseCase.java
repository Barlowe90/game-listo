package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.ListaGameRepositorio;

import java.util.List;
import java.util.UUID;

import com.gamelisto.biblioteca.domain.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarTodasLasListasUseCase implements BuscarTodasLasListasGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  @Transactional
  public List<ListaGameResult> execute(UUID userId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId);

    return listaGameRepositorio.findByUsuarioRefId(result.userUuid).stream()
        .map(ListaGameResult::from)
        .toList();
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(UUID userId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    return new EntradaBuscarListaGame(userUuid);
  }

  private record EntradaBuscarListaGame(UsuarioId userUuid) {}
}
