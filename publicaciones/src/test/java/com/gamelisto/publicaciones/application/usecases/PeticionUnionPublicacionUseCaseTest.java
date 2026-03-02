package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.domain.EstadoPeticion;
import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
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
  @InjectMocks private PeticionUnionPublicacionUseCase useCase;

  @Test
  @DisplayName("debe guardar la petición con estado SOLICITADA y devolver el resultado")
  void debeCrearPeticionConEstadoSolicitada() {
    UUID publicacionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ArgumentCaptor<PeticionUnion> captor = ArgumentCaptor.forClass(PeticionUnion.class);
    when(peticionUnionRepositorio.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    PeticionUnionResult result = useCase.execute(publicacionId, userId);

    PeticionUnion peticion = captor.getValue();
    assertThat(peticion.getEstadoPeticion()).isEqualTo(EstadoPeticion.SOLICITADA);
    assertThat(peticion.getPublicacionId()).isEqualTo(publicacionId);
    assertThat(peticion.getUsuarioId()).isEqualTo(userId);

    assertThat(result.estadoPeticion()).isEqualTo("SOLICITADA");
    assertThat(result.publicacionId()).isEqualTo(publicacionId.toString());
    verify(peticionUnionRepositorio).save(any(PeticionUnion.class));
  }
}
