package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.domain.*;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearPublicacionUseCase - Unit tests")
class CrearPublicacionUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @InjectMocks private CrearPublicacionUseCase useCase;

  @Test
  @DisplayName("debe crear y devolver PublicacionResult con los datos del command")
  void debeCrearPublicacion() {
    UUID autorId = UUID.randomUUID();
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(autorId, 999L, "Busco grupo", "ESP", "NOVATO", "LOGROS", 4);

    ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
    when(publicacionRepositorio.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    PublicacionResult result = useCase.execute(command);

    assertThat(result.autorId()).isEqualTo(autorId.toString());
    assertThat(result.titulo()).isEqualTo("Busco grupo");
    assertThat(result.estadoPublicacion()).isEqualTo("PUBLICADA");
    assertThat(captor.getValue().getEstadoPublicacion()).isEqualTo(EstadoPublicacion.PUBLICADA);
    verify(publicacionRepositorio).save(any(Publicacion.class));
  }

  @Test
  @DisplayName("debe lanzar excepción si el repositorio devuelve null")
  void debeLanzarExcepcionSiRepositorioDevuelveNull() {
    UUID autorId = UUID.randomUUID();
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(autorId, 1L, "Titulo", "ENG", "PRO", "LOGROS", 2);
    when(publicacionRepositorio.save(any())).thenReturn(null);

    org.junit.jupiter.api.Assertions.assertThrows(
        com.gamelisto.publicaciones.application.exceptions.ApplicationException.class,
        () -> useCase.execute(command));
  }
}
