package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
  public Optional<PeticionUnion> findById(UUID id) {
    return mongoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<PeticionUnion> findByPublicacionId(UUID publicacionId) {
    return mongoRepository.findByPublicacionId(publicacionId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<PeticionUnion> findByPublicacionIdAndUsuarioId(
      UUID publicacionId, UUID usuarioId) {
    return mongoRepository
        .findByPublicacionIdAndUsuarioId(publicacionId, usuarioId)
        .map(mapper::toDomain);
  }

  @Override
  public void deleteById(UUID peticionId) {
    mongoRepository.deleteById(peticionId);
  }
}
