package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.PlatformDTO;
import java.util.List;

public interface ObtenerTodasLasPlatformasHandle {
  List<PlatformDTO> execute();
}
