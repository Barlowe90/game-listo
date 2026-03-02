package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbandonarGrupoUseCase - Unit tests")
class AbandonarGrupoUseCaseTest {

  @Mock private GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @Mock private PublicacionRepositorio publicacionRepositorio;
  @InjectMocks private AbandonarGrupoUseCase useCase;

  @Test
  @DisplayName("debe eliminar la membresía si el usuario no es el autor")
  void debeAbandonarGrupo() {
    UUID autorId = UUID.randomUUID();
    UUID otroUsuario = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();

    Publicacion pub =
        Publicacion.reconstitute(
            publicacionId,
            autorId,
            1L,
            "Titulo",
            Idioma.ESP,
            Experiencia.NOVATO,
            EstiloJuego.LOGROS,
            4,
            EstadoPublicacion.PUBLICADA);
    GrupoJuego grupo = GrupoJuego.create(publicacionId);

    when(publicacionRepositorio.findById(publicacionId)).thenReturn(Optional.of(pub));
    when(grupoJuegoRepositorio.findByPublicacionId(publicacionId)).thenReturn(Optional.of(grupo));

    useCase.execute(publicacionId, otroUsuario);

    verify(grupoJuegoUsuarioRepositorio).deleteByGrupoIdAndUsuarioId(grupo.getId(), otroUsuario);
  }

  @Test
  @DisplayName("debe lanzar excepción si el autor intenta abandonar su propio grupo")
  void debeLanzarExcepcionSiElAutorIntentaAbandonar() {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();

    Publicacion pub =
        Publicacion.reconstitute(
            publicacionId,
            autorId,
            1L,
            "Titulo",
            Idioma.ESP,
            Experiencia.NOVATO,
            EstiloJuego.LOGROS,
            4,
            EstadoPublicacion.PUBLICADA);

    when(publicacionRepositorio.findById(publicacionId)).thenReturn(Optional.of(pub));

    assertThatThrownBy(() -> useCase.execute(publicacionId, autorId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("autor no puede abandonar");
  }
}
