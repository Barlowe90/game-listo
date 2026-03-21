package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gamelisto.publicaciones.domain.EstiloJuego;
import com.gamelisto.publicaciones.domain.Experiencia;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import com.gamelisto.publicaciones.domain.Idioma;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.UsuarioRef;
import com.gamelisto.publicaciones.domain.UsuariosRefRepositorio;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
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
@DisplayName("BuscarPublicacionUseCase - Unit tests")
class BuscarPublicacionUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @Mock private GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  @Mock private UsuariosRefRepositorio usuariosRefRepositorio;
  @InjectMocks private BuscarPublicacionUseCase useCase;

  @Test
  @DisplayName("debe devolver el detalle con id plano, grupoId y participantes")
  void debeDevolverElDetalleConGrupoYParticipantes() {
    UUID autorId = UUID.randomUUID();
    UUID jugadorId = UUID.randomUUID();
    Publicacion publicacion = publicacionDe(autorId);
    GrupoJuego grupo = GrupoJuego.create(publicacion.getId());
    GrupoJuegoUsuario autorMiembro = GrupoJuegoUsuario.create(grupo.getId(), UsuarioId.of(autorId));
    GrupoJuegoUsuario jugadorMiembro =
        GrupoJuegoUsuario.create(grupo.getId(), UsuarioId.of(jugadorId));

    when(publicacionRepositorio.findById(publicacion.getId())).thenReturn(Optional.of(publicacion));
    when(grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()))
        .thenReturn(Optional.of(grupo));
    when(grupoJuegoUsuarioRepositorio.findByGrupoId(grupo.getId()))
        .thenReturn(List.of(autorMiembro, jugadorMiembro));
    when(usuariosRefRepositorio.findById(autorId))
        .thenReturn(Optional.of(UsuarioRef.create(autorId, "autor", null)));
    when(usuariosRefRepositorio.findById(jugadorId))
        .thenReturn(Optional.of(UsuarioRef.create(jugadorId, "jugador", null)));

    PublicacionDetalleResult result = useCase.execute(publicacion.getId().value().toString());

    assertThat(result.id()).isEqualTo(publicacion.getId().value().toString());
    assertThat(result.grupoId()).isEqualTo(grupo.getId().value().toString());
    assertThat(result.participantesCount()).isEqualTo(2);
    assertThat(result.plazasDisponibles()).isEqualTo(2);
    assertThat(result.participantes())
        .extracting(UsuarioRefResult::id)
        .containsExactlyInAnyOrder(autorId.toString(), jugadorId.toString());
  }

  private Publicacion publicacionDe(UUID autorId) {
    return Publicacion.create(
        autorId,
        20L,
        "Publicacion de prueba",
        Idioma.ESP,
        Experiencia.NOVATO,
        EstiloJuego.LOGROS,
        4,
        DisponibilidadSemanal.empty());
  }
}
