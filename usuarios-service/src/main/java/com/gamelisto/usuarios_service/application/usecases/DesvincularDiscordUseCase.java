package com.gamelisto.usuarios_service.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;

@Service
public class DesvincularDiscordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DesvincularDiscordUseCase.class);

    private final RepositorioUsuarios repositorioUsuarios;

    public DesvincularDiscordUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public UsuarioDTO execute(String usuarioIdStr) {
        logger.info("Desvinculando cuenta de Discord para usuario: {}", usuarioIdStr);

        UsuarioId usuarioId = UsuarioId.fromString(usuarioIdStr);
        Usuario usuario = repositorioUsuarios.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioIdStr));

        usuario.unlinkDiscord();

        Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

        logger.info("Cuenta de Discord desvinculada exitosamente para usuario: {}", usuarioIdStr);

        return UsuarioDTO.from(usuarioActualizado);
    }
}
