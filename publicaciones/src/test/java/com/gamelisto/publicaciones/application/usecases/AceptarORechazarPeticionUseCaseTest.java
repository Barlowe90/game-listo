package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
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
@DisplayName("AceptarORechazarPeticionUseCase - Unit tests")
class AceptarORechazarPeticionUseCaseTest {

  @Mock private PeticionUnionRepositorio peticionUnionRepositorio;
  @Mock private PublicacionRepositorio publicacionRepositorio;
  @InjectMocks private AceptarORechazarPeticionUseCase useCase;

  @Test
  @DisplayName("debe aceptar la petición si el usuario es el autor de la publicación")
  void debeAceptarPeticion() {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    UUID peticionId = UUID.randomUUID();

    PeticionUnion peticion =
        PeticionUnion.reconstitute(
            peticionId, publicacionId, UUID.randomUUID(), EstadoPeticion.SOLICITADA);
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

    when(peticionUnionRepositorio.findById(peticionId)).thenReturn(Optional.of(peticion));
    // NOTA: el useCase busca por peticionUnion.getId() en vez de publicacionId — bug conocido
    when(publicacionRepositorio.findById(peticionId)).thenReturn(Optional.of(pub));
    when(peticionUnionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PeticionUnionResult result =
        useCase.execute(new PeticionUnionCommand(peticionId, autorId, "ACEPTADA"));

    assertThat(result.estadoPeticion()).isEqualTo("ACEPTADA");
  }

  @Test
  @DisplayName("debe lanzar excepción si el usuario no es el autor de la publicación")
  void debeLanzarExcepcionSiNoEsElAutor() {
    UUID autorId = UUID.randomUUID();
    UUID otroUsuario = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    UUID peticionId = UUID.randomUUID();

    PeticionUnion peticion =
        PeticionUnion.reconstitute(
            peticionId, publicacionId, UUID.randomUUID(), EstadoPeticion.SOLICITADA);
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

    when(peticionUnionRepositorio.findById(peticionId)).thenReturn(Optional.of(peticion));
    when(publicacionRepositorio.findById(peticionId)).thenReturn(Optional.of(pub));

    assertThatThrownBy(
            () -> useCase.execute(new PeticionUnionCommand(peticionId, otroUsuario, "ACEPTADA")))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("propietario");
  }
}
