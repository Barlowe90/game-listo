package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PeticionUnionRepositorioMongo implements PeticionUnionRepositorio {

  private final PeticionUnionRepository mongoRepository;
  private final PeticionUnionMapper mapper;

  @Override
  public PeticionUnion save(PeticionUnion peticionUnion) {
    return mapper.toDomain(mongoRepository.save(mapper.toDocument(peticionUnion)));
  }

  @Override
  public Optional<PeticionUnion> findById(PeticionId id) {
    return mongoRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<PeticionUnion> findByPublicacionId(PublicacionId publicacionId) {
    return mongoRepository.findByPublicacionId(publicacionId.value()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<PeticionUnion> findByPublicacionIdAndUsuarioId(
      PublicacionId publicacionId, UsuarioId usuarioId) {
    return mongoRepository
        .findByPublicacionIdAndUsuarioId(publicacionId.value(), usuarioId.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<PeticionUnion> findByUsuarioId(UsuarioId usuarioId) {
    return mongoRepository.findByUsuarioId(usuarioId.value()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PeticionUnion> findByPublicacionIdIn(List<PublicacionId> publicacionIds) {
    if (publicacionIds == null || publicacionIds.isEmpty()) {
      return List.of();
    }
    List<java.util.UUID> ids = publicacionIds.stream().map(PublicacionId::value).toList();
    return mongoRepository.findByPublicacionIdIn(ids).stream().map(mapper::toDomain).toList();
  }
}
