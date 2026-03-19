package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SolicitudUnionRepositorioMongo implements SolicitudUnionRepositorio {

  private final SolicitudUnionRepository mongoRepository;
  private final SolicitudUnionMapper mapper;

  @Override
  public SolicitudUnion save(SolicitudUnion solicitudUnion) {
    return mapper.toDomain(mongoRepository.save(mapper.toDocument(solicitudUnion)));
  }

  @Override
  public Optional<SolicitudUnion> findById(SolicitudId id) {
    return mongoRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<SolicitudUnion> findByPublicacionId(PublicacionId publicacionId) {
    return mongoRepository.findByPublicacionId(publicacionId.value()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<SolicitudUnion> findByPublicacionIdAndUsuarioId(
      PublicacionId publicacionId, UsuarioId usuarioId) {
    return mongoRepository
        .findByPublicacionIdAndUsuarioId(publicacionId.value(), usuarioId.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<SolicitudUnion> findByUsuarioId(UsuarioId usuarioId) {
    return mongoRepository.findByUsuarioId(usuarioId.value()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<SolicitudUnion> findByPublicacionIdIn(List<PublicacionId> publicacionIds) {
    if (publicacionIds == null || publicacionIds.isEmpty()) {
      return List.of();
    }
    List<java.util.UUID> ids = publicacionIds.stream().map(PublicacionId::value).toList();
    return mongoRepository.findByPublicacionIdIn(ids).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteByPublicacionId(PublicacionId publicacionId) {
    mongoRepository.deleteByPublicacionId(publicacionId.value());
  }
}
