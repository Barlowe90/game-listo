package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AceptarORechazarSolicitudUseCase - Unit tests")
class AceptarORechazarPeticionUseCaseTest {

  @Mock private SolicitudUnionRepositorio solicitudUnionRepositorio;
  @Mock private PublicacionRepositorio publicacionRepositorio;
  @InjectMocks private AceptarORechazarPeticionUseCase useCase;

  @Test
  @DisplayName("debe aceptar la solicitud si el usuario es el autor de la publicación")
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
    Publicacion pub =
        Publicacion.reconstitute(
            publicacionId,
            autorId,
            1L,
            "Titulo",
            Idioma.ESP,
            Experiencia.NOVATO,
            EstiloJuego.LOGROS,
            4);

    when(solicitudUnionRepositorio.findById(SolicitudId.of(peticionId)))
        .thenReturn(Optional.of(peticion));
    // NOTA: el useCase busca por peticionUnion.getPublicacionId().value()
    when(publicacionRepositorio.findById(PublicacionId.of(peticion.getPublicacionId().value())))
        .thenReturn(Optional.of(pub));
    when(solicitudUnionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    SolicitudUnionResult result =
        useCase.execute(new SolicitudUnionCommand(peticionId, autorId, "ACEPTADA"));

    assertThat(result.estadoSolicitud()).isEqualTo("ACEPTADA");
  }

  @Test
  @DisplayName("debe lanzar excepción si el usuario no es el autor de la publicación")
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
            4);

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
  @DisplayName("debe rechazar la solicitud si el usuario es el autor de la publicación")
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
            4);

    when(solicitudUnionRepositorio.findById(SolicitudId.of(peticionId)))
        .thenReturn(Optional.of(peticion));
    // Mantener el mismo comportamiento del use case (busca por id de la petición)
    when(publicacionRepositorio.findById(PublicacionId.of(peticion.getPublicacionId().value())))
        .thenReturn(Optional.of(pub));
    when(solicitudUnionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    SolicitudUnionResult result =
        useCase.execute(new SolicitudUnionCommand(peticionId, autorId, "RECHAZADA"));

    assertThat(result.estadoSolicitud()).isEqualTo("RECHAZADA");
  }
}
