package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public record ImportarBibliotecaSteamCommand(UUID userId, String steamId64) {}
