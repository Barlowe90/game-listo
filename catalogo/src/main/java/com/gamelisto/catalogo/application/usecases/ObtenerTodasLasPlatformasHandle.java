package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.out.PlatformDTO;
import java.util.List;

public interface ObtenerTodasLasPlatformasHandle {
  List<PlatformDTO> execute();
}
