package com.gamelisto.usuarios_service.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gamelisto.usuarios_service.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.events.UsuarioCreado;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.PasswordHash;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.Username;
import com.gamelisto.usuarios_service.domain.exceptions.EmailYaRegistradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsernameYaExisteException;

@Service
public class CrearUsuarioUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CrearUsuarioUseCase.class);
    private static final String ROUTING_KEY_SUFFIX = "usuario.creado";

    private final RepositorioUsuarios repositorioUsuarios;
    private final IUsuarioPublisher eventosPublisher;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;

    public CrearUsuarioUseCase(
            RepositorioUsuarios repositorioUsuarios,
            PasswordEncoder passwordEncoder,
            IUsuarioPublisher eventosPublisher,
            IEmailService emailService) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.passwordEncoder = passwordEncoder;
        this.eventosPublisher = eventosPublisher;
        this.emailService = emailService;
    }

    @Transactional
    public UsuarioDTO execute(CrearUsuarioCommand command) {
        logger.debug("🔍 Iniciando creación de usuario con username: {} y email: {}",
                command.username(), command.email());

        Username username = Username.of(command.username());
        Email email = Email.of(command.email());

        if (repositorioUsuarios.existsByUsername(username)) {
            logger.warn("⚠️ Intento de registro con username ya existente: {}", command.username());
            throw new UsernameYaExisteException(command.username());
        }

        if (repositorioUsuarios.existsByEmail(email)) {
            logger.warn("⚠️ Intento de registro con email ya registrado: {}", command.email());
            throw new EmailYaRegistradoException(command.email());
        }

        String hashedPassword = passwordEncoder.encode(command.password());
        PasswordHash passwordHash = PasswordHash.of(hashedPassword);

        Usuario usuario = Usuario.create(username, email, passwordHash);
        Usuario usuarioGuardado = repositorioUsuarios.save(usuario);

        logger.info("✅ Usuario creado exitosamente - ID: {}, Username: {}",
                usuarioGuardado.getId().value(), usuarioGuardado.getUsername().value());

        // Enviar email de verificación
        String verificationToken = usuarioGuardado.getTokenVerificacion().value();
        emailService.sendVerificationEmail(
                usuarioGuardado.getEmail().value(),
                usuarioGuardado.getUsername().value(),
                verificationToken);

        // Publicar evento de usuario creado
        enviarColaUsuarioCreado(usuarioGuardado);

        return UsuarioDTO.from(usuarioGuardado);
    }

    private void enviarColaUsuarioCreado(Usuario usuarioGuardado) {
        UsuarioCreado evento = UsuarioCreado.of(
                usuarioGuardado.getId().value().toString(),
                usuarioGuardado.getUsername().value(),
                usuarioGuardado.getEmail().value());
        eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);

        logger.debug("📨 Evento 'usuario.creado' publicado para usuario ID: {}",
                usuarioGuardado.getId().value());
    }

}
