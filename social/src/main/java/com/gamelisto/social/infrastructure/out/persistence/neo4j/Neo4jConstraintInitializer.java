package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class Neo4jConstraintInitializer implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(Neo4jConstraintInitializer.class);
  private final Neo4jClient neo4jClient;

  @Override
  public void run(String... args) {
    createConstraint(
        "CREATE CONSTRAINT IF NOT EXISTS FOR (u:User) REQUIRE u.id IS UNIQUE",
        "Constraint de unicidad para :User(id) verificado/creado");

    createConstraint(
        "CREATE CONSTRAINT IF NOT EXISTS FOR (g:Game) REQUIRE g.id IS UNIQUE",
        "Constraint de unicidad para :Game(id) verificado/creado");
  }

  private void createConstraint(String query, String successMessage) {
    try {
      neo4jClient.query(query).run();
      log.info(successMessage);
    } catch (Exception e) {
      log.warn("No se pudo ejecutar el constraint '{}': {}", query, e.getMessage());
    }
  }
}
