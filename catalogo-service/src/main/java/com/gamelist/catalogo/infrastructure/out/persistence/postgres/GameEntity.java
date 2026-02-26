package com.gamelist.catalogo.infrastructure.out.persistence.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ElementCollection
  @CollectionTable(name = "game_alternative_names", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "alternative_name")
  private List<String> alternativeNames = new ArrayList<>();

  @Column(name = "cover_url")
  private String coverUrl;

  @ElementCollection
  @CollectionTable(name = "game_dlcs", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "dlc_id")
  private List<Long> dlcIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_expanded_games", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "expanded_game_id")
  private List<Long> expandedGames = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_expansions", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "expansion_id")
  private List<Long> expansionIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_external_games", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "external_url", length = 500)
  private List<String> externalGames = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_franchises", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "franchise_name")
  private List<String> franchises = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_modes", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "mode_name")
  private List<String> gameModes = new ArrayList<>();

  @Column(name = "game_status")
  private String gameStatus;

  @Column(name = "game_type")
  private String gameType;

  @ElementCollection
  @CollectionTable(name = "game_genres", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "genre_name")
  private List<String> genres = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_companies", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "company_name")
  private List<String> involvedCompanies = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_keywords", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "keyword")
  private List<String> keywords = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_multiplayer_mode_ids", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "multiplayer_mode_id")
  private List<Long> multiplayerModeIds = new ArrayList<>();

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "parent_game_id")
  private Long parentGameId;

  @ElementCollection
  @CollectionTable(name = "game_platforms", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "platform_name")
  private List<String> platforms = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_perspectives", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "perspective_name")
  private List<String> playerPerspectives = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_remakes", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "remake_id")
  private List<Long> remakeIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_remasters", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "remaster_id")
  private List<Long> remasterIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_screenshots", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "screenshots")
  private List<String> screenshots = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_similar_games", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "similar_game_id")
  private List<Long> similarGames = new ArrayList<>();

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @ElementCollection
  @CollectionTable(name = "game_themes", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "theme_name")
  private List<String> themes = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "game_videos", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "videos")
  private List<String> videos = new ArrayList<>();
}
