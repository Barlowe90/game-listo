package com.gamelisto.usuarios_service.application.usecases;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.SolicitarRestablecimientoCommand;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;

@Service
public class SolicitarRestablecimientoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SolicitarRestablecimientoUseCase.class);

    private final RepositorioUsuarios repositorioUsuarios;
    private final IEmailService emailService;

    public SolicitarRestablecimientoUseCase(
            RepositorioUsuarios repositorioUsuarios,
            IEmailService emailService) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.emailService = emailService;
    }

    @Transactional
    public void execute(SolicitarRestablecimientoCommand command) {
        logger.debug("🔐 Procesando solicitud de restablecimiento para email: {}", command.email());

        Email email = Email.of(command.email());
        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.info("⚠️ Solicitud de restablecimiento para email no registrado: {}", command.email());
            return; // No revelar que el email no existe (seguridad)
        }

        Usuario usuario = usuarioOpt.get();
        usuario.generarTokenRestablecimiento();
        repositorioUsuarios.save(usuario);

        logger.info("✅ Token de restablecimiento generado para usuario: {}", usuario.getUsername().value());

        // Enviar email con el enlace de restablecimiento
        emailService.sendPasswordResetEmail(
                usuario.getEmail().value(),
                usuario.getUsername().value(),
                usuario.getTokenRestablecimiento().value());
    }
}
