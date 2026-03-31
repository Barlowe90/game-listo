package com.gamelisto.graphql.client;
import com.gamelisto.graphql.model.Publicacion;
import com.gamelisto.graphql.model.GrupoJuego;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.List;
@Service
public class PublicacionesClient {
    private final RestClient publicacionesClient;
    public PublicacionesClient(RestClient publicacionesClient) {
        this.publicacionesClient = publicacionesClient;
    }
    public List<Publicacion> getPublicacionesByGame(String gameId) {
        try {
            return publicacionesClient.get()
                    .uri("/v1/publicaciones/game/{gameId}", gameId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Publicacion>>() {});
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) return List.of();
            throw e;
        }
    }
    public GrupoJuego getGrupoJuego(String grupoId) {
        try {
            return publicacionesClient.get()
                    .uri("/v1/publicaciones/grupos/{grupoId}", grupoId)
                    .retrieve()
                    .body(GrupoJuego.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) return null;
            throw e;
        }
    }
}
