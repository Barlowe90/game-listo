package com.gamelisto.usuarios.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.Username;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
@Transactional
class RepositorioUsuariosIntegrationTest {

  @Autowired private RepositorioUsuarios repositorioUsuarios;

  @Test
  @DisplayName("Debe guardar y buscar usuario por email")
  void debeGuardarYBuscarPorEmail() {
    Usuario usuario =
        Usuario.create(
            Username.of("email-user"),
            Email.of("email-user@test.com"),
            PasswordHash.of("$2a$10$hash"));

    repositorioUsuarios.save(usuario);

    Optional<Usuario> encontrado = repositorioUsuarios.findByEmail(Email.of("email-user@test.com"));

    assertThat(encontrado).isPresent();
    assertThat(encontrado.get().getUsername().value()).isEqualTo("email-user");
  }

  @Test
  @DisplayName("Debe vincular Discord y buscar por Discord User ID")
  void debeVincularDiscordYBuscar() {
    Usuario usuario =
        Usuario.create(
            Username.of("discord-user"),
            Email.of("discord-user@test.com"),
            PasswordHash.of("$2a$10$hash"));
    usuario = repositorioUsuarios.save(usuario);

    DiscordUserId discordId = DiscordUserId.of("123456789");
    usuario.linkDiscord(discordId);
    repositorioUsuarios.save(usuario);

    Optional<Usuario> encontrado = repositorioUsuarios.findByDiscordUserId(discordId);

    assertThat(encontrado).isPresent();
    assertThat(encontrado.get().hasDiscordLinked()).isTrue();
    assertThat(encontrado.get().getDiscordUserId().value()).isEqualTo("123456789");
  }

  @Test
  @DisplayName("Debe retornar vacio si no existe usuario con email")
  void debeRetornarVacioSiNoExisteEmail() {
    Optional<Usuario> noEncontrado =
        repositorioUsuarios.findByEmail(Email.of("noexiste@example.com"));

    assertThat(noEncontrado).isEmpty();
  }
}
