package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.events.EmailVerificado;
import com.gamelisto.usuarios_service.domain.exceptions.TokenVerificacionInvalidoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioYaVerificadoException;

@Service
public class VerificarEmailUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;
    private final IUsuarioPublisher eventosPublisher;
    private static final String ROUTING_KEY_SUFFIX = "usuario.verificado";

    public VerificarEmailUseCase(RepositorioUsuarios repositorioUsuarios, IUsuarioPublisher eventosPublisher) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.eventosPublisher = eventosPublisher;
    }

    @Transactional
    public void execute(VerificarEmailCommand command) {
        TokenVerificacion token = TokenVerificacion.of(command.token());

        Usuario usuario = repositorioUsuarios.findByTokenVerificacion(token)
                .orElseThrow(() -> new TokenVerificacionInvalidoException(command.token()));

        try {
            usuario.verificarEmail(token);
        } catch (IllegalStateException e) {
            throw new UsuarioYaVerificadoException(usuario.getEmail().value());
        } catch (IllegalArgumentException e) {
            throw new TokenVerificacionInvalidoException(command.token(), e.getMessage());
        }

        EmailVerificado evento = EmailVerificado.of(
            usuario.getId().value().toString(),
            usuario.getEmail().value()
        );
        eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);

        repositorioUsuarios.save(usuario);
    }
}
