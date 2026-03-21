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
@DisplayName("BuscarPublicacionesUsuarioUseCase - Unit tests")
class BuscarPublicacionesUsuarioUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @InjectMocks private BuscarPublicacionesUsuarioUseCase useCase;

  @Test
  @DisplayName("debe devolver el grupoId cuando la publicacion ya tiene grupo")
  void debeDevolverGrupoIdCuandoLaPublicacionYaTieneGrupo() {
    UUID autorId = UUID.randomUUID();
    Publicacion publicacion = publicacionDe(autorId);
    GrupoJuego grupo = GrupoJuego.create(publicacion.getId());

    when(publicacionRepositorio.findByAutorId(any())).thenReturn(List.of(publicacion));
    when(grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()))
        .thenReturn(Optional.of(grupo));

    List<PublicacionResult> result = useCase.execute(autorId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().id()).isEqualTo(publicacion.getId().value().toString());
    assertThat(result.getFirst().grupoId()).isEqualTo(grupo.getId().value().toString());
    verify(grupoJuegoRepositorio).findByPublicacionId(publicacion.getId());
  }

  @Test
  @DisplayName("debe devolver grupoId nulo cuando todavia no existe grupo asociado")
  void debeDevolverGrupoIdNuloCuandoNoExisteGrupoAsociado() {
    UUID autorId = UUID.randomUUID();
    Publicacion publicacion = publicacionDe(autorId);

    when(publicacionRepositorio.findByAutorId(any())).thenReturn(List.of(publicacion));
    when(grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()))
        .thenReturn(Optional.empty());

    List<PublicacionResult> result = useCase.execute(autorId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().grupoId()).isNull();
  }

  private Publicacion publicacionDe(UUID autorId) {
    return Publicacion.create(
        autorId,
        10L,
        "Grupo de prueba",
        Idioma.ESP,
        Experiencia.NOVATO,
        EstiloJuego.LOGROS,
        4,
        DisponibilidadSemanal.empty());
  }
}
