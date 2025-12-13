package com.gamelisto.usuarios_service.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios_service.domain.errors.EntidadNoEncontrada;
import com.gamelisto.usuarios_service.domain.exceptions.DiscordYaVinculadoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUsername;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.infrastructure.discord.DiscordClient;
import com.gamelisto.usuarios_service.infrastructure.discord.DiscordTokenResponse;
import com.gamelisto.usuarios_service.infrastructure.discord.DiscordUserResponse;

/**
 * Caso de uso para vincular una cuenta de Discord a un usuario.
 * 
 * Flujo:
 * 1. Intercambia el código de autorización por un access token de Discord
 * 2. Obtiene la información del usuario de Discord
 * 3. Valida que la cuenta de Discord no esté vinculada a otro usuario
 * 4. Vincula la cuenta de Discord al usuario
 */
@Service
public class VincularDiscordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(VincularDiscordUseCase.class);

    private final RepositorioUsuarios repositorioUsuarios;
    private final DiscordClient discordClient;

    public VincularDiscordUseCase(
            RepositorioUsuarios repositorioUsuarios,
            DiscordClient discordClient) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.discordClient = discordClient;
    }

    @Transactional
    public UsuarioDTO execute(VincularDiscordCommand command) {
        logger.info("Vinculando cuenta de Discord para usuario: {}", command.usuarioId());

        // 1. Obtener el usuario
        UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());
        Usuario usuario = repositorioUsuarios.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontrada("Usuario no encontrado con ID: " + command.usuarioId()));

        // 2. Intercambiar código por access token
        DiscordTokenResponse tokenResponse = discordClient.exchangeCode(
                command.code(),
                command.redirectUri()
        );

        // 3. Obtener información del usuario de Discord
        DiscordUserResponse discordUser = discordClient.getUserInfo(tokenResponse.accessToken());

        // 4. Validar que la cuenta de Discord no esté vinculada a otro usuario
        DiscordUserId discordUserId = DiscordUserId.of(discordUser.id());
        repositorioUsuarios.findByDiscordUserId(discordUserId).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(usuarioId)) {
                throw new DiscordYaVinculadoException(discordUser.id());
            }
        });

        // 5. Vincular la cuenta de Discord
        DiscordUsername discordUsername = DiscordUsername.of(discordUser.username());
        usuario.linkDiscord(discordUserId, discordUsername);

        // 6. Guardar cambios
        Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

        logger.info("Cuenta de Discord vinculada exitosamente: {} -> {}", 
                discordUser.username(), command.usuarioId());

        return UsuarioDTO.from(usuarioActualizado);
    }
}
