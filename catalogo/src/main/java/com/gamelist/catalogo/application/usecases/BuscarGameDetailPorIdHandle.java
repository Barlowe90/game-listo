package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDetailDTO;

public interface BuscarGameDetailPorIdHandle {
  GameDetailDTO execute(BuscarGameDetailPorIdCommand command);
}
