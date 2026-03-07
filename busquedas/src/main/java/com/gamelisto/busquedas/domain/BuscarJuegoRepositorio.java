package com.gamelisto.busquedas.domain;

import java.util.List;

public interface BuscarJuegoRepositorio {

  void upsert(BuscarJuegoDoc doc);

  List<BuscarJuegoDoc> suggest(String prefix, int size);
}
