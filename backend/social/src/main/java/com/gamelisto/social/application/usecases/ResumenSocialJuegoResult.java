package com.gamelisto.social.application.usecases;

import java.util.List;

public record ResumenSocialJuegoResult(
    long amigosDeseadoCount,
    long amigosJugandoCount,
    List<UserRefResult> amigosDeseadoPreview,
    List<UserRefResult> amigosJugandoPreview) {}
