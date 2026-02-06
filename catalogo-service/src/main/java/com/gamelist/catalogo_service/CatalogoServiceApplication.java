package com.gamelist.catalogo_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogoServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(CatalogoServiceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CatalogoServiceApplication.class, args);

    logger.info("🟢 Microservicio catalogo arrancado.");
  }
}
