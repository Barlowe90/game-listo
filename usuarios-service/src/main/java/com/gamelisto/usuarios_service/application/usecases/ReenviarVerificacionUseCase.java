package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.ReenviarVerificacionCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioYaVerificadoException;

@Service
public class ReenviarVerificacionUseCase {

    private final RepositorioUsuarios repositorioUsuarios;
    // private final EmailService emailService;

    public ReenviarVerificacionUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public void execute(ReenviarVerificacionCommand command) {
        Email email = Email.of(command.email());

        Usuario usuario = repositorioUsuarios.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(command.email()));

        if (usuario.getStatus() != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
            throw new UsuarioYaVerificadoException(command.email());
        }

        // Regenera token y actualiza expiración
        usuario.generarTokenVerificacion();

        repositorioUsuarios.save(usuario);

        // TODO: Implementar envío de email de verificación
        // emailService.sendVerificationEmail(
        //     usuario.getEmail().value(),
        //     usuario.getTokenVerificacion().value()
        // );
    }
}
