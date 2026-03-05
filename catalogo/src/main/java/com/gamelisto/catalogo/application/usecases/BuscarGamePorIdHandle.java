package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelisto.catalogo.application.dto.out.GameDTO;

public interface BuscarGamePorIdHandle {
  GameDTO execute(BuscarGamePorIdCommand command);
}
