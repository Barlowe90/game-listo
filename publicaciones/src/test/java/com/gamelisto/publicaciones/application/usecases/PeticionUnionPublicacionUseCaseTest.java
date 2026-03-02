package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
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
@DisplayName("PeticionUnionPublicacionUseCase - Unit tests")
class PeticionUnionPublicacionUseCaseTest {

  @Mock private PeticionUnionRepositorio peticionUnionRepositorio;
  @InjectMocks private CrearSolicitudUnionUseCase useCase;

  @Test
  @DisplayName("debe guardar la petición con estado SOLICITADA y devolver el resultado")
  void debeCrearPeticionConEstadoSolicitada() {
    UUID publicacionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ArgumentCaptor<PeticionUnion> captor = ArgumentCaptor.forClass(PeticionUnion.class);
    when(peticionUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId)))
        .thenReturn(Optional.empty());
    when(peticionUnionRepositorio.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    PeticionUnionResult result = useCase.execute(publicacionId, userId);

    PeticionUnion peticion = captor.getValue();
    assertThat(peticion.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.SOLICITADA);
    assertThat(peticion.getPublicacionId().value()).isEqualTo(publicacionId);
    assertThat(peticion.getUsuarioId().value()).isEqualTo(userId);

    assertThat(result.estadoSolicitud()).isEqualTo("SOLICITADA");
    assertThat(result.publicacionId()).isEqualTo(publicacionId.toString());
    verify(peticionUnionRepositorio).save(any(PeticionUnion.class));
  }

  @Test
  @DisplayName(
      "debe lanzar excepción si ya existe una solicitud del mismo usuario para la publicacion")
  void debeLanzarSiYaExiste() {
    UUID publicacionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    PeticionUnion existente =
        PeticionUnion.reconstitute(
            PeticionId.of(UUID.randomUUID()),
            PublicacionId.of(publicacionId),
            UsuarioId.of(userId),
            EstadoSolicitud.SOLICITADA);

    when(peticionUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId)))
        .thenReturn(Optional.of(existente));

    assertThatThrownBy(() -> useCase.execute(publicacionId, userId))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Ya existe una solicitud");

    verify(peticionUnionRepositorio, never()).save(any());
  }
}
