package com.gamelisto.busquedas.infrastructure.opensearch;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import com.gamelisto.busquedas.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
      List<String> inputs = construirInputParaSuggester(doc);

      List<String> deduplicated = eliminarDuplicados(inputs);

      Map<String, Object> paqueteOpenSearch = prepararPaqueteParaOpenSerach(doc, deduplicated);

      IndexRequest<Map<String, Object>> request = guardarEnOpenSearch(doc, paqueteOpenSearch);

      client.index(request);

    } catch (OpenSearchException | IOException e) {
      logger.error(
          "Error al hacer upsert en OpenSearch para gameId={}: {}",
          doc.getGameId(),
          e.getMessage());
      throw new InfrastructureException("OpenSearch no disponible para upsert", e);
    }
  }

  private IndexRequest<Map<String, Object>> guardarEnOpenSearch(
      BuscarJuegoDoc doc, Map<String, Object> paqueteOpenSearch) {
    return IndexRequest.of(
        b -> b.index(writeIndex).id(String.valueOf(doc.getGameId())).document(paqueteOpenSearch));
  }

  private static @NonNull Map<String, Object> prepararPaqueteParaOpenSerach(
      BuscarJuegoDoc doc, List<String> deduplicated) {
    Map<String, Object> document = new LinkedHashMap<>();
    document.put("gameId", doc.getGameId());
    document.put("title", doc.getTitle());
    document.put("alternativeNames", doc.getAlternativeNames());

    Map<String, Object> nameSuggest = new HashMap<>();
    nameSuggest.put("input", deduplicated);
    document.put("nameSuggest", nameSuggest);
    return document;
  }

  private static @NonNull List<String> construirInputParaSuggester(BuscarJuegoDoc doc) {
    List<String> inputs = new ArrayList<>();
    inputs.add(doc.getTitle().trim());
    for (String alt : doc.getAlternativeNames()) {
      if (alt != null && !alt.isBlank()) {
        inputs.add(alt.trim());
      }
    }
    return inputs;
  }

  @Override
  public List<BuscarJuegoDoc> suggest(String prefix, int size) {
    try {
      SearchRequest request =
          SearchRequest.of(
              b ->
                  b.index(readIndex)
                      .source(s -> s.filter(f -> f.includes("gameId", "title")))
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
      throw new InfrastructureException("OpenSearch no disponible para suggest", e);
    }
  }

  private List<String> eliminarDuplicados(List<String> inputs) {
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
