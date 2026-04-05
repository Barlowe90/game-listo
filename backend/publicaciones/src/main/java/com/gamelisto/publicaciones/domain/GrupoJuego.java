package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class GrupoJuego {
    private final GrupoId id;
    private final PublicacionId publicacionId;
    private final Instant fechaCreacion;

    private GrupoJuego(GrupoId id, PublicacionId publicacionId, Instant fechaCreacion) {
        this.id = id;
        this.publicacionId = publicacionId;
        this.fechaCreacion = fechaCreacion;
    }

    public static GrupoJuego create(PublicacionId publicacionId) {
        return new GrupoJuego(GrupoId.of(UUID.randomUUID()), publicacionId, Instant.now());
    }

    public static GrupoJuego reconstitute(
            GrupoId id, PublicacionId publicacionId, Instant fechaCreacion) {
        return new GrupoJuego(id, publicacionId, fechaCreacion);
    }
}
