package com.gamelisto.graphql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.catalogo.url:http://catalogo:8082}")
    private String catalogoUrl;

    @Value("${services.publicaciones.url:http://publicaciones:8084}")
    private String publicacionesUrl;

    @Bean("catalogoRestClient")
    public RestClient catalogoRestClient() {
        return RestClient.builder()
                .baseUrl(catalogoUrl)
                .build();
    }

    @Bean("publicacionesRestClient")
    public RestClient publicacionesRestClient() {
        return RestClient.builder()
                .baseUrl(publicacionesUrl)
                .build();
    }
}

