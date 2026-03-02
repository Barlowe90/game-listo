package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearPublicacionUseCase implements CrearPublicacionHandler {

  private final PublicacionRepositorio publicacionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;

  @Override
  public PublicacionResult execute(CrearPublicacionCommand command) {
    UUID autorUuid = command.autorId();
    Long gameId = command.gameId();
    String titulo = command.titulo();
    Idioma idioma = Idioma.valueOf(command.idioma());
    Experiencia experiencia = Experiencia.valueOf(command.experiencia());
    EstiloJuego estiloJuego = EstiloJuego.valueOf(command.estiloJuego());
    int jugadoresMaximos = command.jugadoresMaximos();

    Publicacion publicacion =
        Publicacion.create(
            autorUuid, gameId, titulo, idioma, experiencia, estiloJuego, jugadoresMaximos);

    Publicacion publicacionGuardada = publicacionRepositorio.save(publicacion);

    if (publicacionGuardada == null) {
      throw new ApplicationException("No se pudo crear la publicacion");
    }

    GrupoJuego grupo = GrupoJuego.create(publicacionGuardada.getId());
    GrupoJuego grupoGuardado = grupoJuegoRepositorio.save(grupo);

    agregarAutorComoMiembroDelGrupo(grupoGuardado, autorUuid);

    return PublicacionResult.from(publicacionGuardada);
  }

  private void agregarAutorComoMiembroDelGrupo(GrupoJuego grupoGuardado, UUID autorUuid) {
    if (!grupoJuegoUsuarioRepositorio.existsByGrupoIdAndUsuarioId(
        grupoGuardado.getId(), UsuarioId.of(autorUuid))) {
      GrupoJuegoUsuario miembro =
          GrupoJuegoUsuario.create(grupoGuardado.getId(), UsuarioId.of(autorUuid));
      grupoJuegoUsuarioRepositorio.save(miembro);
    }
  }
}
