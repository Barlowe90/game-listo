package com.gamelisto.graphql.client;

import com.gamelisto.graphql.model.Game;
import com.gamelisto.graphql.model.GameMedia;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class CatalogoClient {
    private final RestClient catalogoClient;

    public CatalogoClient(@Qualifier("catalogoRestClient") RestClient catalogoClient) {
        this.catalogoClient = catalogoClient;
    }

    public Game getGameById(Integer id) {
        try {
            return catalogoClient.get()
                    .uri("/v1/catalogo/games/{id}", id)
                    .retrieve()
                    .body(Game.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404 || e.getStatusCode().value() == 400) return null;
            throw e;
        }
    }

    public GameMedia getGameMedia(Integer id) {
        try {
            return catalogoClient.get()
                    .uri("/v1/catalogo/games/{id}/detail", id)
                    .retrieve()
                    .body(GameMedia.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404 || e.getStatusCode().value() == 400) return null;
            throw e;
        }
    }
}
