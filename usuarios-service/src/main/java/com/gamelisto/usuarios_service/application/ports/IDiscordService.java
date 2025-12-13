package com.gamelisto.usuarios_service.application.ports;

import com.gamelisto.usuarios_service.application.dto.DiscordTokenCommand;
import com.gamelisto.usuarios_service.application.dto.DiscordUserCommand;

public interface IDiscordService {
    
    DiscordTokenCommand exchangeCode(String code, String redirectUri);
    
    DiscordUserCommand getUserInfo(String accessToken);
}
