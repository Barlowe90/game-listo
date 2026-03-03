package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitudUnionPublicacionUseCase - Unit tests")
class SolicitudUnionPublicacionUseCaseTest {

  @Mock private SolicitudUnionRepositorio solicitudUnionRepositorio;
  @InjectMocks private CrearSolicitudUnionUseCase useCase;

  @Test
  @DisplayName("debe guardar la solicitud con estado SOLICITADA y devolver el resultado")
  void debeCrearPeticionConEstadoSolicitada() {
    UUID publicacionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ArgumentCaptor<SolicitudUnion> captor = ArgumentCaptor.forClass(SolicitudUnion.class);
    when(solicitudUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId)))
        .thenReturn(Optional.empty());
    when(solicitudUnionRepositorio.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    SolicitudUnionResult result = useCase.execute(publicacionId, userId);

    SolicitudUnion peticion = captor.getValue();
    assertThat(peticion.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.SOLICITADA);
    assertThat(peticion.getPublicacionId().value()).isEqualTo(publicacionId);
    assertThat(peticion.getUsuarioId().value()).isEqualTo(userId);

    assertThat(result.estadoSolicitud()).isEqualTo("SOLICITADA");
    assertThat(result.publicacionId()).isEqualTo(publicacionId.toString());
    verify(solicitudUnionRepositorio).save(any(SolicitudUnion.class));
  }

  @Test
  @DisplayName(
      "debe lanzar excepción si ya existe una solicitud del mismo usuario para la publicacion")
  void debeLanzarSiYaExiste() {
    UUID publicacionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    SolicitudUnion existente =
        SolicitudUnion.reconstitute(
            SolicitudId.of(UUID.randomUUID()),
            PublicacionId.of(publicacionId),
            UsuarioId.of(userId),
            EstadoSolicitud.SOLICITADA);

    when(solicitudUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId)))
        .thenReturn(Optional.of(existente));

    assertThatThrownBy(() -> useCase.execute(publicacionId, userId))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Ya existe una solicitud");

    verify(solicitudUnionRepositorio, never()).save(any());
  }
}
