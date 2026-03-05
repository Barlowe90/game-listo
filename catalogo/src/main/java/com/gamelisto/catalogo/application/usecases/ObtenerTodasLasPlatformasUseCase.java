package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.out.PlatformDTO;
import com.gamelisto.catalogo.domain.PlataformaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObtenerTodasLasPlatformasUseCase implements ObtenerTodasLasPlatformasHandle {

  private final PlataformaRepositorio platformRepository;

  @Override
  public List<PlatformDTO> execute() {
    return platformRepository.findAll().stream().map(PlatformDTO::from).toList();
  }
}
