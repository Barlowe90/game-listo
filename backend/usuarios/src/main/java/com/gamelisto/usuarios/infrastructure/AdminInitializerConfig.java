package com.gamelisto.usuarios.infrastructure;

import java.util.UUID;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Rol;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.UsuarioEntity;
import com.gamelisto.usuarios.infrastructure.out.persistence.postgres.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AdminInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializerConfig.class);

    @Bean
    CommandLineRunner initAdminUser(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_PASSWORD}") String adminPassword) {

        return args -> {
            String adminEmail = "adrian.r.r@um.es";

            if (usuarioJpaRepository.existsByEmail(adminEmail)) {
                return;
            }

            UsuarioEntity admin = new UsuarioEntity();
            admin.setId(UUID.randomUUID());
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setAvatar(null);
            admin.setRole(Rol.ADMIN);
            admin.setStatus(EstadoUsuario.ACTIVO);
            admin.setDiscordUserId(null);
            admin.setTokenVerificacion(null);
            admin.setTokenVerificacionExpiracion(null);
            admin.setTokenRestablecimiento(null);
            admin.setTokenRestablecimientoExpiracion(null);

            usuarioJpaRepository.save(admin);

            logger.info("Usuario Admin creado correctamente");
        };
    }
}
