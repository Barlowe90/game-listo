package com.gamelisto.busquedas.infrastructure.opensearch;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import com.gamelisto.busquedas.infrastructure.exceptions.OpenSearchUnavailableException;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.CompletionSuggestOption;
import org.opensearch.client.opensearch.core.search.Suggest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BuscarJuegoRepositorioOpenSearch implements BuscarJuegoRepositorio {

  private static final Logger logger =
      LoggerFactory.getLogger(BuscarJuegoRepositorioOpenSearch.class);

  private final OpenSearchClient client;

  @Value("${opensearch.index.write}")
  private String writeIndex;

  @Value("${opensearch.index.read}")
  private String readIndex;

  @Override
  public void upsert(BuscarJuegoDoc doc) {
    try {
      // Construir los inputs para el completion suggester
      List<String> inputs = new ArrayList<>();
      inputs.add(doc.getTitle().trim());
      for (String alt : doc.getAlternativeNames()) {
        if (alt != null && !alt.isBlank()) {
          inputs.add(alt.trim());
        }
      }
      // Deduplicar manteniendo orden (case-insensitive)
      List<String> deduplicated = deduplicateInputs(inputs);

      // Construir el documento como Map
      Map<String, Object> document = new LinkedHashMap<>();
      document.put("gameId", doc.getGameId());
      document.put("title", doc.getTitle());
      document.put("alternativeNames", doc.getAlternativeNames());

      Map<String, Object> nameSuggest = new HashMap<>();
      nameSuggest.put("input", deduplicated);
      document.put("nameSuggest", nameSuggest);

      IndexRequest<Map<String, Object>> request =
          IndexRequest.of(
              b -> b.index(writeIndex).id(String.valueOf(doc.getGameId())).document(document));

      client.index(request);
      logger.info("Upsert en OpenSearch: gameId={}, title={}", doc.getGameId(), doc.getTitle());

    } catch (OpenSearchException | IOException e) {
      logger.error(
          "Error al hacer upsert en OpenSearch para gameId={}: {}",
          doc.getGameId(),
          e.getMessage());
      throw new OpenSearchUnavailableException("OpenSearch no disponible para upsert", e);
    }
  }

  @Override
  public void delete(long gameId) {
    try {
      client.delete(d -> d.index(writeIndex).id(String.valueOf(gameId)));
      logger.info("Documento eliminado de OpenSearch: gameId={}", gameId);
    } catch (OpenSearchException | IOException e) {
      logger.error(
          "Error al eliminar documento en OpenSearch para gameId={}: {}", gameId, e.getMessage());
      throw new OpenSearchUnavailableException("OpenSearch no disponible para delete", e);
    }
  }

  @Override
  public List<BuscarJuegoDoc> suggest(String prefix, int size) {
    try {
      SearchRequest request =
          SearchRequest.of(
              b ->
                  b.index(readIndex)
                      .source(s -> s.filter(f -> f.includes("gameId", "title")))
                      .size(0)
                      .suggest(
                          sg ->
                              sg.suggesters(
                                  "game-names",
                                  sv ->
                                      sv.prefix(prefix)
                                          .completion(
                                              c ->
                                                  c.field("nameSuggest")
                                                      .size(size)
                                                      .skipDuplicates(true)))));

      SearchResponse<Map> response = client.search(request, Map.class);

      List<BuscarJuegoDoc> results = new ArrayList<>();
      Map<String, List<Suggest<Map>>> suggestions = response.suggest();

      if (suggestions == null || !suggestions.containsKey("game-names")) {
        return Collections.emptyList();
      }

      for (Suggest<Map> suggestion : suggestions.get("game-names")) {
        for (CompletionSuggestOption<Map> option : suggestion.completion().options()) {
          Map<String, Object> source = option.source();
          if (source == null) continue;

          Object gameIdObj = source.get("gameId");
          Object titleObj = source.get("title");

          if (gameIdObj == null || titleObj == null) continue;

          long gameId = ((Number) gameIdObj).longValue();
          String title = (String) titleObj;
          results.add(new BuscarJuegoDoc(gameId, title, Collections.emptyList()));
        }
      }

      return results;

    } catch (OpenSearchException | IOException e) {
      logger.error(
          "Error al consultar sugerencias en OpenSearch para prefix='{}': {}",
          prefix,
          e.getMessage());
      throw new OpenSearchUnavailableException("OpenSearch no disponible para suggest", e);
    }
  }

  private List<String> deduplicateInputs(List<String> inputs) {
    List<String> result = new ArrayList<>();
    List<String> lowerSeen = new ArrayList<>();
    for (String input : inputs) {
      if (input == null || input.isBlank()) continue;
      String lower = input.toLowerCase();
      if (!lowerSeen.contains(lower)) {
        lowerSeen.add(lower);
        result.add(input);
      }
    }
    return result;
  }
}
