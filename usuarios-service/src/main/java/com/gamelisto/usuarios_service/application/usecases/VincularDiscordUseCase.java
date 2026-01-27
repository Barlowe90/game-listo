package com.gamelisto.usuarios_service.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios_service.domain.exceptions.DiscordYaVinculadoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUsername;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;

@Service
public class VincularDiscordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(VincularDiscordUseCase.class);

    private final RepositorioUsuarios repositorioUsuarios;

    public VincularDiscordUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public UsuarioDTO execute(VincularDiscordCommand command) {
        logger.info("Vinculando cuenta de Discord para usuario: {}", command.usuarioId());

        // 1. Obtener el usuario
        UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());
        Usuario usuario = repositorioUsuarios.findById(usuarioId)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + command.usuarioId()));

        // 2. Validar que la cuenta de Discord no esté vinculada a otro usuario
        DiscordUserId discordUserId = DiscordUserId.of(command.discordUserId());
        repositorioUsuarios.findByDiscordUserId(discordUserId).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(usuarioId)) {
                throw new DiscordYaVinculadoException(command.discordUserId());
            }
        });

        // 3. Vincular la cuenta de Discord
        DiscordUsername discordUsername = DiscordUsername.of(command.discordUsername());
        usuario.linkDiscord(discordUserId, discordUsername);

        // 4. Guardar cambios
        Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

        logger.info("Cuenta de Discord vinculada exitosamente: {} -> {}",
                command.discordUsername(), command.usuarioId());

        return UsuarioDTO.from(usuarioActualizado);
    }
}
