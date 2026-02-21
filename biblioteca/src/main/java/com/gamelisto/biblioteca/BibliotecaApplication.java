package com.gamelisto.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class BibliotecaApplication {

  private static final Logger logger = LoggerFactory.getLogger(BibliotecaApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(BibliotecaApplication.class, args);
    logger.info("🟢 Microservicio biblioteca arrancado.");
  }
}
