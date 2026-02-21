package com.gamelist.catalogo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CatalogoServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(CatalogoServiceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CatalogoServiceApplication.class, args);

    logger.info("🟢 Microservicio catálogo arrancado.");
  }
}
