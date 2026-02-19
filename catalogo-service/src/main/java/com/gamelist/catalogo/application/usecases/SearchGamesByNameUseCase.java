package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.queries.SearchGamesQuery;
import com.gamelist.catalogo.application.dto.results.GameDTO;
import com.gamelist.catalogo.application.dto.results.PlatformDTO;
import com.gamelist.catalogo.domain.catalog.Platform;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso para buscar juegos por nombre.
 *
 * <p>Nota: la búsqueda por fragmento de nombre (containing / full-text) se ha delegado al
 * microservicio de búsqueda (search-service).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchGamesByNameUseCase {

  private final IGameRepository gameRepository;
  private final IPlatformRepository platformRepository;

  @Transactional(readOnly = true)
  public Page<GameDTO> execute(SearchGamesQuery query) {
    log.info(
        "Búsqueda por nombre deshabilitada en catalogo-service. Nombre: '{}', página: {}, tamaño: {}",
        query.name(),
        query.page(),
        query.size());

    // La lógica de búsqueda por nombre se ha movido a `search-service`.
    // Lanzamos una excepción clara para evitar llamadas silenciosas a un método inexistente.
    throw new UnsupportedOperationException(
        "La búsqueda por nombre se realiza ahora en el microservicio 'search-service'. "
            + "No use SearchGamesByNameUseCase en catalogo-service.");
  }

  // Conservamos el conversor a DTO por si se reutiliza la conversión en otros casos de uso.
  private GameDTO convertToDTO(Game game) {
    // Obtener plataformas del juego
    Set<Platform> platforms =
        game.getPlatformIds().stream()
            .map(platformId -> platformRepository.findById(platformId).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Set<PlatformDTO> platformDTOs =
        platforms.stream()
            .map(
                p ->
                    new PlatformDTO(
                        p.getId().value(), p.getName().value(), p.getAbbreviation().value()))
            .collect(Collectors.toSet());

    return new GameDTO(
        game.getId().value(),
        game.getName().value(),
        game.getSummary().value(),
        game.getCoverUrl().value(),
        platformDTOs);
  }
}
