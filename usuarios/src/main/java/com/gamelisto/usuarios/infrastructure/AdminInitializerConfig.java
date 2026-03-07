package com.gamelisto.usuarios.infrastructure;

import java.util.UUID;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Rol;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.entity.UsuarioEntity;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.repository.UsuarioJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializerConfig {

  @Bean
  CommandLineRunner initAdminUser(
      UsuarioJpaRepository usuarioJpaRepository, PasswordEncoder passwordEncoder) {

    return args -> {
      String adminEmail = "admin@gamelisto.com";

      if (usuarioJpaRepository.existsByEmail(adminEmail)) {
        return;
      }

      UsuarioEntity admin = new UsuarioEntity();
      admin.setId(UUID.randomUUID());
      admin.setUsername("admin");
      admin.setEmail(adminEmail);
      admin.setPasswordHash(passwordEncoder.encode("admin1234"));
      admin.setAvatar(null);
      admin.setRole(Rol.ADMIN);
      admin.setLanguage(Idioma.ESP);
      admin.setStatus(EstadoUsuario.ACTIVO);
      admin.setDiscordUserId(null);
      admin.setDiscordUsername(null);
      admin.setTokenVerificacion(null);
      admin.setTokenVerificacionExpiracion(null);
      admin.setTokenRestablecimiento(null);
      admin.setTokenRestablecimientoExpiracion(null);

      usuarioJpaRepository.save(admin);
    };
  }
}
