package com.gamelisto.usuarios_service.infrastructure.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

/**
 * Response de Discord OAuth2 token endpoint.
 */
public record DiscordTokenResponse(
    @JsonProperty("access_token")
    @NonNull String accessToken,
    
    @JsonProperty("token_type")
    @NonNull String tokenType,
    
    @JsonProperty("expires_in")
    int expiresIn,
    
    @JsonProperty("refresh_token")
    String refreshToken,
    
    @NonNull String scope
) {}
