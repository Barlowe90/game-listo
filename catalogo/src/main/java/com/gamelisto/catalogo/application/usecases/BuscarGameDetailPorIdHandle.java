package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelisto.catalogo.application.dto.out.GameDetailDTO;

public interface BuscarGameDetailPorIdHandle {
  GameDetailDTO execute(BuscarGameDetailPorIdCommand command);
}
