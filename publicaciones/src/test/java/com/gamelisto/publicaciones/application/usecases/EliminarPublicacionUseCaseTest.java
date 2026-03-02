package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
@DisplayName("EliminarPublicacionUseCase - Unit tests")
class EliminarPublicacionUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @InjectMocks private EliminarPublicacionUseCase useCase;

  private Publicacion publicacionDe(UUID autorId) {
    return Publicacion.create(
        autorId, 1L, "Titulo", Idioma.ESP, Experiencia.NOVATO, EstiloJuego.LOGROS, 4);
  }

  @Test
  @DisplayName("debe eliminar la publicación si el usuario es el autor")
  void debeEliminarSiEsElAutor() {
    UUID autorId = UUID.randomUUID();
    Publicacion pub = publicacionDe(autorId);
    when(publicacionRepositorio.findById(pub.getId())).thenReturn(Optional.of(pub));

    useCase.execute(pub.getId(), autorId);

    verify(publicacionRepositorio).deleteById(pub.getId());
  }

  @Test
  @DisplayName("debe lanzar excepción si el usuario no es el autor")
  void debeLanzarExcepcionSiNoEsElAutor() {
    UUID autorId = UUID.randomUUID();
    UUID otroUsuario = UUID.randomUUID();
    Publicacion pub = publicacionDe(autorId);
    when(publicacionRepositorio.findById(pub.getId())).thenReturn(Optional.of(pub));

    assertThatThrownBy(() -> useCase.execute(pub.getId(), otroUsuario))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("propietario");

    verify(publicacionRepositorio, never()).deleteById(any());
  }

  @Test
  @DisplayName("debe lanzar excepción si la publicación no existe")
  void debeLanzarExcepcionSiNoExiste() {
    UUID id = UUID.randomUUID();
    when(publicacionRepositorio.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(id, UUID.randomUUID()))
        .isInstanceOf(ApplicationException.class);
  }
}
