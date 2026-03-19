package com.gamelisto.biblioteca.domain;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import java.util.Objects;

public final class Rating {

  private static final double MIN_RATING = 0.0;
  private static final double MAX_RATING = 10.0;
  private static final double STEP = 0.25;
  private static final double EPSILON = 1e-9;

  private final double value;

  private Rating(double value) {
    validateRating(value);
    this.value = value;
  }

  private static void validateRating(double rating) {
    if (!Double.isFinite(rating)) {
      throw new DomainException("El rating debe ser un numero finito entre 0.0 y 10.0");
    }
    if (rating < MIN_RATING || rating > MAX_RATING) {
      throw new DomainException("El rating debe estar entre 0.0 y 10.0");
    }

    double scaledRating = rating / STEP;
    double nearestStep = Math.rint(scaledRating);

    if (Math.abs(scaledRating - nearestStep) > EPSILON) {
      throw new DomainException("El rating debe avanzar en incrementos de 0.25");
    }
  }

  public static Rating of(double value) {
    return new Rating(value);
  }

  public double value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Rating rating = (Rating) o;
    return Double.compare(rating.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
