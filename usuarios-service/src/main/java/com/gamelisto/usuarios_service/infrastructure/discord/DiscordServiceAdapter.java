package com.gamelisto.usuarios_service.infrastructure.discord;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.gamelisto.usuarios_service.application.dto.DiscordTokenCommand;
import com.gamelisto.usuarios_service.application.dto.DiscordUserCommand;
import com.gamelisto.usuarios_service.application.ports.IDiscordService;

@Service
public class DiscordServiceAdapter implements IDiscordService {

    private final DiscordClient discordClient;

    public DiscordServiceAdapter(DiscordClient discordClient) {
        this.discordClient = discordClient;
    }

    @Override
    public DiscordTokenCommand exchangeCode(String code, String redirectUri) {
        Objects.requireNonNull(code, "Authorization code cannot be null");
        Objects.requireNonNull(redirectUri, "Redirect URI cannot be null");
        
        DiscordTokenResponse response = discordClient.exchangeCode(code, redirectUri);
        return new DiscordTokenCommand(
            Objects.requireNonNull(response.accessToken(), "Discord access token cannot be null")
        );
    }

    @Override
    public DiscordUserCommand getUserInfo(String accessToken) {
        Objects.requireNonNull(accessToken, "Access token cannot be null");
        
        DiscordUserResponse response = discordClient.getUserInfo(accessToken);
        return new DiscordUserCommand(
            Objects.requireNonNull(response.id(), "Discord user id cannot be null"),
            Objects.requireNonNull(response.username(), "Discord username cannot be null")
        );
    }
}
