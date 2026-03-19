package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.Set;

import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("EditarPublicacionUseCase - Unit tests")
class EditarPublicacionUseCaseTest {

  @Mock private PublicacionRepositorio publicacionRepositorio;
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @InjectMocks private EditarPublicacionUseCase useCase;

  private Publicacion publicacionDe(UUID autorId) {
    return Publicacion.create(
        autorId,
        100L,
        "Titulo original",
        Idioma.ESP,
        Experiencia.NOVATO,
        EstiloJuego.LOGROS,
        4,
        DisponibilidadSemanal.empty());
  }

  @Test
  @DisplayName("debe actualizar el título y devolver el resultado")
  void debeEditarTitulo() {
    UUID autorId = UUID.randomUUID();
    Publicacion pub = publicacionDe(autorId);
    GrupoJuego grupo = GrupoJuego.create(PublicacionId.of(pub.getId().value()));
    when(publicacionRepositorio.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(publicacionRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(grupoJuegoRepositorio.findByPublicacionId(pub.getId())).thenReturn(Optional.of(grupo));

    Map<String, Set<String>> disponibilidad = Map.of("MIERCOLES", Set.of("DIA"));
    EditarPublicacionCommand command =
        new EditarPublicacionCommand(
            pub.getId().value(),
            autorId,
            "Nuevo titulo",
            "ENG",
            "PRO",
            "DISFRUTAR_DEL_JUEGO",
            6,
            disponibilidad);

    PublicacionResult result = useCase.execute(command);

    assertThat(result.titulo()).isEqualTo("Nuevo titulo");
    assertThat(result.idioma()).isEqualTo("ENG");
    assertThat(result.jugadoresMaximos()).isEqualTo(6);
    assertThat(result.grupoId()).isEqualTo(grupo.getId().value().toString());
    verify(publicacionRepositorio).save(any(Publicacion.class));
  }

  @Test
  @DisplayName("debe lanzar excepción si el usuario no es el autor")
  void debeLanzarExcepcionSiNoEsElAutor() {
    UUID autorId = UUID.randomUUID();
    Publicacion pub = publicacionDe(autorId);
    when(publicacionRepositorio.findById(pub.getId())).thenReturn(Optional.of(pub));

    Map<String, Set<String>> disponibilidad = Map.of("DOMINGO", Set.of("TARDE"));
    EditarPublicacionCommand command =
        new EditarPublicacionCommand(
            pub.getId().value(),
            UUID.randomUUID(),
            "Hack",
            "ESP",
            "NOVATO",
            "LOGROS",
            2,
            disponibilidad);

    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("propietario");
  }
}
