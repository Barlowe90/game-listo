package com.gamelisto.usuarios_service.infrastructure.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP para comunicarse con la API de Discord OAuth2.
 * Maneja el intercambio de código de autorización por access token
 * y la obtención de información del usuario.
 */
@Component
public class DiscordClient {

    private static final Logger logger = LoggerFactory.getLogger(DiscordClient.class);
    private static final String TOKEN_URL = "https://discord.com/api/v10/oauth2/token";
    private static final String USER_URL = "https://discord.com/api/v10/users/@me";

    private final RestTemplate restTemplate;
    private final @NonNull String clientId;
    private final @NonNull String clientSecret;

    public DiscordClient(
            RestTemplate restTemplate,
            @Value("${discord.client-id}") @NonNull String clientId,
            @Value("${discord.client-secret}") @NonNull String clientSecret) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "RestTemplate no puede ser null");
        this.clientId = Objects.requireNonNull(clientId, "Discord client-id no puede ser null");
        this.clientSecret = Objects.requireNonNull(clientSecret, "Discord client-secret no puede ser null");
    }

    /**
     * Intercambia el código de autorización por un access token de Discord.
     */
    public DiscordTokenResponse exchangeCode(@NonNull String code, @NonNull String redirectUri) {
        logger.info("Intercambiando código de autorización con Discord");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<DiscordTokenResponse> response = restTemplate.exchange(
                    TOKEN_URL,
                    Objects.requireNonNull(HttpMethod.POST),
                    request,
                    DiscordTokenResponse.class
            );

            logger.info("Token de Discord obtenido exitosamente");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al intercambiar código con Discord: {}", e.getMessage());
            throw new DiscordApiException("No se pudo intercambiar el código de autorización con Discord", e);
        }
    }

    /**
     * Obtiene la información del usuario de Discord usando el access token.
     */
    public DiscordUserResponse getUserInfo(@NonNull String accessToken) {
        logger.info("Obteniendo información del usuario de Discord");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<DiscordUserResponse> response = restTemplate.exchange(
                    USER_URL,
                    Objects.requireNonNull(HttpMethod.GET),
                    request,
                    DiscordUserResponse.class
            );

            logger.info("Información del usuario de Discord obtenida exitosamente");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener información del usuario de Discord: {}", e.getMessage());
            throw new DiscordApiException("No se pudo obtener la información del usuario de Discord", e);
        }
    }
}
