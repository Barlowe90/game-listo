package com.gamelisto.busquedas.domain;

import java.util.List;

public interface BuscarJuegoRepositorio {

  void upsert(BuscarJuegoDoc doc);

  void delete(long gameId);

  List<BuscarJuegoDoc> suggest(String prefix, int size);
}
