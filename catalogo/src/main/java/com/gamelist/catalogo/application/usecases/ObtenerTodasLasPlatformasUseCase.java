package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.PlatformDTO;
import com.gamelist.catalogo.domain.repositories.RepositorioPlataforma;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObtenerTodasLasPlatformasUseCase {

  private final RepositorioPlataforma platformRepository;

  public ObtenerTodasLasPlatformasUseCase(RepositorioPlataforma platformRepository) {
    this.platformRepository = platformRepository;
  }

  @Transactional(readOnly = true)
  public List<PlatformDTO> execute() {
    return platformRepository.findAll().stream().map(PlatformDTO::from).toList();
  }
}
