package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarGrupoJuegoUseCase implements BuscarGrupoJuegoHandle {

  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;

  @Override
  public GrupoJuegoResult execute(UUID grupoJuegoId) {
    GrupoJuego grupoJuego =
        grupoJuegoRepositorio
            .findById(GrupoId.of(grupoJuegoId))
            .orElseThrow(() -> new ApplicationException("Grupo juego no encontrado"));

    List<java.util.UUID> userIds =
        grupoJuegoUsuarioRepositorio.findByGrupoId(grupoJuego.getId()).stream()
            .map(g -> g.getUsuarioId().value())
            .toList();

    List<UsuarioRef> usuarios =
        userIds.stream()
            .map(id -> usuariosRefRepositorio.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();

    return GrupoJuegoResult.from(grupoJuego, usuarios);
  }
}
