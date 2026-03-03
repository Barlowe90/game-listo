package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import java.util.List;

public interface SugerirJuegosHandle {
  List<BuscarJuegoDoc> execute(String query, Integer requestedSize);
}
