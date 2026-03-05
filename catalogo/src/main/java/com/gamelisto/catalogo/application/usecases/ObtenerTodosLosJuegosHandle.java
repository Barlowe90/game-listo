package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.out.GameDTO;
import java.util.List;

public interface ObtenerTodosLosJuegosHandle {
  List<GameDTO> execute();
}
