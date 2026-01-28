package com.gamelisto.usuarios_service.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.ReenviarVerificacionCommand;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioYaVerificadoException;

@Service
public class ReenviarVerificacionUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReenviarVerificacionUseCase.class);
    
    private final RepositorioUsuarios repositorioUsuarios;
    private final IEmailService emailService;

    public ReenviarVerificacionUseCase(
            RepositorioUsuarios repositorioUsuarios,
            IEmailService emailService) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.emailService = emailService;
    }

    @Transactional
    public void execute(ReenviarVerificacionCommand command) {
        logger.debug("🔄 Reenviando verificación para email: {}", command.email());
        
        Email email = Email.of(command.email());

        Usuario usuario = repositorioUsuarios.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(command.email()));

        if (usuario.getStatus() != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
            logger.warn("⚠️ Intento de reenvío de verificación para usuario ya verificado: {}", command.email());
            throw new UsuarioYaVerificadoException(command.email());
        }

        // Regenera token y actualiza expiración
        usuario.generarTokenVerificacion();
        repositorioUsuarios.save(usuario);
        
        logger.info("✅ Nuevo token de verificación generado para usuario: {}", usuario.getUsername().value());

        // Enviar email de verificación
        emailService.sendVerificationEmail(
            usuario.getEmail().value(),
            usuario.getUsername().value(),
            usuario.getTokenVerificacion().value()
        );
    }
}
