package com.gamelisto.busquedas.infrastructure.opensearch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.CompletionProperty;
import org.opensearch.client.opensearch._types.mapping.KeywordProperty;
import org.opensearch.client.opensearch._types.mapping.LongNumberProperty;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TextProperty;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.PutAliasRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Crea los índices de OpenSearch al arrancar si no existen. games-write: índice donde se indexan
 * los documentos. games-read: alias (o índice) desde el que se consultan las sugerencias.
 */
@Component
@RequiredArgsConstructor
public class OpenSearchIndexInitializer {

  private static final Logger logger = LoggerFactory.getLogger(OpenSearchIndexInitializer.class);

  private final OpenSearchClient client;

  @Value("${opensearch.index.write}")
  private String writeIndex;

  @Value("${opensearch.index.read}")
  private String readIndex;

  @PostConstruct
  public void init() {
    try {
      // Crear índice de escritura siempre que no exista
      crearIndiceConMapping(writeIndex);

      // Si readIndex es el mismo que writeIndex no hace falta alias
      if (readIndex != null && !readIndex.equals(writeIndex)) {
        // Si existe un índice con el nombre readIndex, no creamos alias (se asume índice físico)
        boolean readExists =
            client.indices().exists(ExistsRequest.of(r -> r.index(readIndex))).value();
        if (readExists) {
          logger.info(
              "Índice de lectura '{}' ya existe como índice físico, no se crea alias.", readIndex);
        } else {
          // Si no existe, intentamos crear un alias readIndex que apunte a writeIndex
          try {
            client.indices().putAlias(PutAliasRequest.of(p -> p.index(writeIndex).name(readIndex)));
            logger.info("Alias '{}' creado apuntando a '{}'.", readIndex, writeIndex);
          } catch (Exception e) {
            logger.error(
                "No se pudo crear alias '{}' apuntando a '{}': {}",
                readIndex,
                writeIndex,
                e.getMessage(),
                e);
          }
        }
      } else {
        // Si son iguales, asegurar que el índice exista (crear si hacía falta)
        crearIndiceConMapping(readIndex);
      }
    } catch (Exception e) {
      logger.error("Error inicializando índices/alias de OpenSearch: {}", e.getMessage(), e);
    }
  }

  private void crearIndiceConMapping(String indexName) {
    try {
      boolean exists = client.indices().exists(ExistsRequest.of(r -> r.index(indexName))).value();

      if (exists) {
        logger.info("Índice '{}' ya existe, se omite la creación.", indexName);
        return;
      }

      client
          .indices()
          .create(
              CreateIndexRequest.of(
                  r ->
                      r.index(indexName)
                          .mappings(
                              m ->
                                  m.properties(
                                      Map.of(
                                          "gameId",
                                              Property.of(
                                                  p -> p.long_(LongNumberProperty.of(l -> l))),
                                          "title",
                                              Property.of(p -> p.text(TextProperty.of(t -> t))),
                                          "alternativeNames",
                                              Property.of(
                                                  p -> p.keyword(KeywordProperty.of(k -> k))),
                                          "nameSuggest",
                                              Property.of(
                                                  p ->
                                                      p.completion(
                                                          CompletionProperty.of(c -> c))))))));

      logger.info(
          "Índice '{}' creado correctamente con mapping de completion suggester.", indexName);

    } catch (Exception e) {
      logger.error("No se pudo crear el índice '{}': {}", indexName, e.getMessage(), e);
    }
  }
}
