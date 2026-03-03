package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDTO;

public interface BuscarGamePorIdHandle {
  GameDTO execute(BuscarGamePorIdCommand command);
}
