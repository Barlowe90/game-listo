package com.gamelisto.graphql.model;

import java.util.List;

public record GameMedia(
    Integer gameId,
    List<String> screenshots,
    List<String> videos
) {}
