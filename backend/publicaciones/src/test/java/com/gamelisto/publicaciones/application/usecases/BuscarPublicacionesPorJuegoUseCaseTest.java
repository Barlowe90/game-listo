package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.publicaciones.domain.EstiloJuego;
import com.gamelisto.publicaciones.domain.Experiencia;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.Idioma;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarPublicacionesPorJuegoUseCase - Unit tests")
class BuscarPublicacionesPorJuegoUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @InjectMocks private BuscarPublicacionesPorJuegoUseCase useCase;

  @Test
  @DisplayName("debe devolver el grupoId cuando la publicacion del juego ya tiene grupo")
  void debeDevolverGrupoIdCuandoLaPublicacionDelJuegoYaTieneGrupo() {
    Publicacion publicacion = publicacionDe();
    GrupoJuego grupo = GrupoJuego.create(publicacion.getId());

    when(publicacionRepositorio.findByGameId(any())).thenReturn(List.of(publicacion));
    when(grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()))
        .thenReturn(Optional.of(grupo));

    List<PublicacionResult> result = useCase.execute(50L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().id()).isEqualTo(publicacion.getId().value().toString());
    assertThat(result.getFirst().grupoId()).isEqualTo(grupo.getId().value().toString());
    verify(grupoJuegoRepositorio).findByPublicacionId(publicacion.getId());
  }

  private Publicacion publicacionDe() {
    return Publicacion.create(
        UUID.randomUUID(),
        50L,
        "Partida cooperativa",
        Idioma.ESP,
        Experiencia.NOVATO,
        EstiloJuego.LOGROS,
        4,
        DisponibilidadSemanal.empty());
  }
}
