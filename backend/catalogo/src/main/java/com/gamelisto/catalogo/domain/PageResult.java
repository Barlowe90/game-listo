package com.gamelisto.catalogo.domain;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PageResult<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
    List<R> mappedContent =
        content.stream().map(item -> mapper.apply(item)).collect(Collectors.toList());
    return new PageResult<>(mappedContent, page, size, totalElements, totalPages);
  }
}
