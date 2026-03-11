package com.gamelisto.social.application.usecases;

import java.util.UUID;

public record UserRefResult(UUID id, String username, String avatar) {}
