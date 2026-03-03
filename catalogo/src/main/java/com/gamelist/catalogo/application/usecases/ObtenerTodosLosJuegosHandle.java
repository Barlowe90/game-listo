package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.GameDTO;
import java.util.List;

public interface ObtenerTodosLosJuegosHandle {
  List<GameDTO> execute();
}
