package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.PageResult;

public interface ObtenerTodosLosJuegosHandle {
  PageResult<GameCardResult> execute(ObtenerTodosLosJuegosCommand command);
}
