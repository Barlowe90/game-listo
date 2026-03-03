package com.gamelist.catalogo.infrastructure.integration;

import com.gamelist.catalogo.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("PlatformRepositoryPostgres - Tests de Integracion")
class PlatformRepositoryPostgresIntegrationTest {
  @Autowired private PlataformaRepositorio platformRepository;

  private Platform crearPlataforma(long id, String nombre, String abrev) {
    return Platform.create(
        PlatformId.of(id),
        PlatformName.of(nombre),
        PlatformAbbreviation.of(abrev),
        null,
        null,
        null);
  }

  @Test
  @DisplayName("Debe listar todas las plataformas guardadas")
  void debeListarTodasLasPlatformas() {
    platformRepository.saveAll(
        List.of(
            crearPlataforma(101L, "Xbox Series X", "XSX"),
            crearPlataforma(102L, "Nintendo Switch", "Switch")));
    assertThat(platformRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
  }
}
