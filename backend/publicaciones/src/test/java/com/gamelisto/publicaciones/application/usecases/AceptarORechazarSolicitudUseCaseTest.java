package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.EstiloJuego;
import com.gamelisto.publicaciones.domain.Experiencia;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import com.gamelisto.publicaciones.domain.Idioma;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.FranjaHoraria;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AceptarORechazarSolicitudUseCase - Unit tests")
class AceptarORechazarSolicitudUseCaseTest {

  @Mock private SolicitudUnionRepositorio solicitudUnionRepositorio;
  @Mock private PublicacionRepositorio publicacionRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @Mock private GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  @InjectMocks private AceptarORechazarPeticionUseCase useCase;

  @Test
  @DisplayName("debe aceptar la solicitud y agregar al usuario al grupo")
  void debeAceptarPeticion() {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    UUID peticionId = UUID.randomUUID();

    SolicitudUnion peticion =
        SolicitudUnion.reconstitute(
            SolicitudId.of(peticionId),
            PublicacionId.of(publicacionId),
            UsuarioId.of(UUID.randomUUID()),
            EstadoSolicitud.SOLICITADA);
    GrupoJuego grupo = GrupoJuego.create(PublicacionId.of(publicacionId));
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
            DisponibilidadSemanal.of(Map.of(DiaSemana.VIERNES, Set.of(FranjaHoraria.NOCHE))));

    when(solicitudUnionRepositorio.findById(SolicitudId.of(peticionId)))
        .thenReturn(Optional.of(peticion));
    when(publicacionRepositorio.findById(PublicacionId.of(peticion.getPublicacionId().value())))
        .thenReturn(Optional.of(pub));
    when(grupoJuegoRepositorio.findByPublicacionId(peticion.getPublicacionId()))
        .thenReturn(Optional.of(grupo));
    when(grupoJuegoUsuarioRepositorio.existsByGrupoIdAndUsuarioId(grupo.getId(), peticion.getUsuarioId()))
        .thenReturn(false);
    when(grupoJuegoUsuarioRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(solicitudUnionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    SolicitudUnionResult result =
        useCase.execute(new SolicitudUnionCommand(peticionId, autorId, "ACEPTADA"));

    assertThat(result.estadoSolicitud()).isEqualTo("ACEPTADA");
    verify(grupoJuegoUsuarioRepositorio).save(any(GrupoJuegoUsuario.class));
  }

  @Test
  @DisplayName("debe lanzar excepcion si el usuario no es el autor de la publicacion")
  void debeLanzarExcepcionSiNoEsElAutor() {
    UUID autorId = UUID.randomUUID();
    UUID otroUsuario = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    UUID peticionId = UUID.randomUUID();

    SolicitudUnion peticion =
        SolicitudUnion.reconstitute(
            SolicitudId.of(peticionId),
            PublicacionId.of(publicacionId),
            UsuarioId.of(UUID.randomUUID()),
            EstadoSolicitud.SOLICITADA);
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
            DisponibilidadSemanal.of(Map.of(DiaSemana.LUNES, Set.of(FranjaHoraria.TARDE))));

    when(solicitudUnionRepositorio.findById(SolicitudId.of(peticionId)))
        .thenReturn(Optional.of(peticion));
    when(publicacionRepositorio.findById(PublicacionId.of(peticion.getPublicacionId().value())))
        .thenReturn(Optional.of(pub));

    assertThatThrownBy(
            () -> useCase.execute(new SolicitudUnionCommand(peticionId, otroUsuario, "ACEPTADA")))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("propietario");
  }

  @Test
  @DisplayName("debe rechazar la solicitud sin agregar al usuario al grupo")
  void debeRechazarPeticion() {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    UUID peticionId = UUID.randomUUID();

    SolicitudUnion peticion =
        SolicitudUnion.reconstitute(
            SolicitudId.of(peticionId),
            PublicacionId.of(publicacionId),
            UsuarioId.of(UUID.randomUUID()),
            EstadoSolicitud.SOLICITADA);
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
            DisponibilidadSemanal.of(Map.of(DiaSemana.LUNES, Set.of(FranjaHoraria.TARDE))));

    when(solicitudUnionRepositorio.findById(SolicitudId.of(peticionId)))
        .thenReturn(Optional.of(peticion));
    when(publicacionRepositorio.findById(PublicacionId.of(peticion.getPublicacionId().value())))
        .thenReturn(Optional.of(pub));
    when(solicitudUnionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    SolicitudUnionResult result =
        useCase.execute(new SolicitudUnionCommand(peticionId, autorId, "RECHAZADA"));

    assertThat(result.estadoSolicitud()).isEqualTo("RECHAZADA");
    verify(grupoJuegoRepositorio, never()).findByPublicacionId(any());
    verify(grupoJuegoUsuarioRepositorio, never()).save(any());
  }
}
