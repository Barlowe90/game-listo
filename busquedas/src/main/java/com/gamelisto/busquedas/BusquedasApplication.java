package com.gamelisto.busquedas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BusquedasApplication {

  private static final Logger logger = LoggerFactory.getLogger(BusquedasApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(BusquedasApplication.class, args);

    logger.info("🟢 Microservicio busquedas arrancado.");
  }
}
