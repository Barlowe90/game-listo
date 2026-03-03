package com.gamelisto.publicaciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PublicacionesApplication {

  private static final Logger logger = LoggerFactory.getLogger(PublicacionesApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(PublicacionesApplication.class, args);
    logger.info("🟢 Microservicio publicaciones arrancado.");
  }
}
