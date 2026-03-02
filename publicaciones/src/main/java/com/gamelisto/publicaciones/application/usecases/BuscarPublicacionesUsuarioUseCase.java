package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarPublicacionesUsuarioUseCase implements BuscarPublicacionesUsuarioHandler {

  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<PublicacionResult> execute(UUID userId) {
    return publicacionRepositorio.findByAutorId(UsuarioId.of(userId)).stream()
        .map(PublicacionResult::from)
        .toList();
  }
}
