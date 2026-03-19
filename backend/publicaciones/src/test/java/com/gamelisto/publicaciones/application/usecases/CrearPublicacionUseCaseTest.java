package com.gamelisto.publicaciones.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
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
  @Mock private GrupoJuegoRepositorio grupoJuegoRepositorio;
  @Mock private GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  @InjectMocks private CrearPublicacionUseCase useCase;

  @Test
  @DisplayName("debe crear la publicacion y agregar al autor al grupo")
  void debeCrearPublicacion() {
    UUID autorId = UUID.randomUUID();
    Map<String, Set<String>> disponibilidad = Map.of("VIERNES", Set.of("NOCHE"));
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(
            autorId, 999L, "Busco grupo", "ESP", "NOVATO", "LOGROS", 4, disponibilidad);

    ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
    when(publicacionRepositorio.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
    when(grupoJuegoRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(grupoJuegoUsuarioRepositorio.existsByGrupoIdAndUsuarioId(any(), any())).thenReturn(false);
    when(grupoJuegoUsuarioRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PublicacionResult result = useCase.execute(command);

    assertThat(result.autorId()).isEqualTo(autorId.toString());
    assertThat(result.titulo()).isEqualTo("Busco grupo");
    verify(publicacionRepositorio).save(any(Publicacion.class));
    verify(grupoJuegoUsuarioRepositorio).save(any(GrupoJuegoUsuario.class));
  }

  @Test
  @DisplayName("debe lanzar excepcion si el repositorio devuelve null")
  void debeLanzarExcepcionSiRepositorioDevuelveNull() {
    UUID autorId = UUID.randomUUID();
    Map<String, Set<String>> disponibilidad = Map.of("LUNES", Set.of("TARDE"));
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(
            autorId, 1L, "Titulo", "ENG", "PRO", "LOGROS", 2, disponibilidad);
    when(publicacionRepositorio.save(any())).thenReturn(null);

    Assertions.assertThrows(ApplicationException.class, () -> useCase.execute(command));
  }
}
