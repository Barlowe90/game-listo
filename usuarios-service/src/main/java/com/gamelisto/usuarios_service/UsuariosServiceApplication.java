package com.gamelisto.usuarios_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gamelisto.usuarios_service.infrastructure.api.rest.UsuariosController;

@SpringBootApplication
public class UsuariosServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(UsuariosServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UsuariosServiceApplication.class, args);

		logger.info("✅ === Microservicio usuarios funcionando === ");
	}

}
