package com.gamelist.catalogo.infrastructure.integration;

import com.gamelist.catalogo.domain.catalog.*;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("PlatformRepositoryPostgres - Tests de Integracion")
class PlatformRepositoryPostgresIntegrationTest {
  @Autowired private IPlatformRepository platformRepository;

  private Platform crearPlataforma(long id, String nombre, String abrev) {
    return Platform.create(
        PlatformId.of(id), PlatformName.of(nombre), PlatformAbbreviation.of(abrev));
  }

  @Test
  @DisplayName("Debe guardar y recuperar una plataforma por ID")
  void debeGuardarYRecuperarPlataforma() {
    platformRepository.save(crearPlataforma(6L, "PC (Microsoft Windows)", "PC"));
    Optional<Platform> r = platformRepository.findById(PlatformId.of(6L));
    assertThat(r).isPresent();
    assertThat(r.get().getName().value()).isEqualTo("PC (Microsoft Windows)");
  }

  @Test
  @DisplayName("Debe hacer upsert de plataforma existente")
  void debeHacerUpsertDePlataformaExistente() {
    platformRepository.save(crearPlataforma(48L, "PlayStation 4", "PS4"));
    platformRepository.save(
        Platform.create(
            PlatformId.of(48L),
            PlatformName.of("PlayStation 4 Updated"),
            PlatformAbbreviation.of("PS4")));
    Optional<Platform> r = platformRepository.findById(PlatformId.of(48L));
    assertThat(r).isPresent();
    assertThat(r.get().getName().value()).isEqualTo("PlayStation 4 Updated");
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

  @Test
  @DisplayName("Debe guardar multiples plataformas con saveAll")
  void debeSaveAllGuardaVariasPlatformas() {
    List<Platform> guardadas =
        platformRepository.saveAll(
            List.of(
                crearPlataforma(201L, "Mega Drive", "MD"),
                crearPlataforma(202L, "Super Nintendo", "SNES"),
                crearPlataforma(203L, "Game Boy", "GB")));
    assertThat(guardadas).hasSize(3);
    assertThat(platformRepository.findById(PlatformId.of(202L))).isPresent();
  }

  @Test
  @DisplayName("Debe devolver Optional vacio para ID inexistente")
  void debeDevolverEmptyParaIdInexistente() {
    assertThat(platformRepository.findById(PlatformId.of(99999L))).isEmpty();
  }
}
