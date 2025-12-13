package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.exceptions.TokenVerificacionInvalidoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioYaVerificadoException;

@Service
public class VerificarEmailUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public VerificarEmailUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
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

        repositorioUsuarios.save(usuario);
    }
}
