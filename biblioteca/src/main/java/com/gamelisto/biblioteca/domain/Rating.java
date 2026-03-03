package com.gamelisto.biblioteca.domain;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;

import java.util.Objects;

public final class Rating {

    private final double value;

    private Rating(double value) {
        validateRating(value);
        this.value = value;
    }

    private static void validateRating(double rating) {
        if (!Double.isFinite(rating)) {
            throw new DomainException("El rating debe ser un número finito entre 0.0 y 5.0");
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new DomainException("El rating debe estar entre 0.0 y 5.0");
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
