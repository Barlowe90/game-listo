package com.gamelisto.usuarios_service.domain.errors;

public class EntidadNoEncontrada extends RuntimeException {
	public EntidadNoEncontrada(String mensaje) {
		super(mensaje);
	}
}