package com.gamelisto.usuarios_service.infrastructure.discord;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response de Discord User endpoint (GET /users/@me).
 */
public record DiscordUserResponse(
    String id,
    String username,
    String discriminator,
    
    @JsonProperty("global_name")
    String globalName,
    
    String avatar,
    Boolean bot,
    Boolean system,
    
    @JsonProperty("mfa_enabled")
    Boolean mfaEnabled,
    
    String banner,
    
    @JsonProperty("accent_color")
    Integer accentColor,
    
    String locale,
    Boolean verified,
    String email,
    Integer flags,
    
    @JsonProperty("premium_type")
    Integer premiumType,
    
    @JsonProperty("public_flags")
    Integer publicFlags
) {}
