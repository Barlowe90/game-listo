package com.gamelisto.usuarios_service.application.usecases;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.SolicitarRestablecimientoCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;

@Service
public class SolicitarRestablecimientoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SolicitarRestablecimientoUseCase.class);

    private final RepositorioUsuarios repositorioUsuarios;

    public SolicitarRestablecimientoUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public void execute(SolicitarRestablecimientoCommand command) {
        Email email = Email.of(command.email());

        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.info("Solicitud de restablecimiento para email no registrado: {}", command.email());
            return;
        }

        Usuario usuario = usuarioOpt.get();

        usuario.generarTokenRestablecimiento();
        repositorioUsuarios.save(usuario);

        // Enviar email con el enlace de restablecimiento

        logger.info("Token de restablecimiento generado y email enviado para usuario: {}", usuario.getUsername().value());
    }
}
