package com.gamelisto.busquedas.infrastructure.opensearch;

import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class OpenSearchConfig {

  @Value("${opensearch.url}")
  private String opensearchUrl;

  @Bean
  public OpenSearchClient openSearchClient() throws URISyntaxException {
    URI uri = new URI(opensearchUrl);

    ApacheHttpClient5Transport transport =
        ApacheHttpClient5TransportBuilder.builder(
                new org.apache.hc.core5.http.HttpHost(
                    uri.getScheme(), uri.getHost(), uri.getPort()))
            .setMapper(new JacksonJsonpMapper())
            .build();

    return new OpenSearchClient(transport);
  }
}
