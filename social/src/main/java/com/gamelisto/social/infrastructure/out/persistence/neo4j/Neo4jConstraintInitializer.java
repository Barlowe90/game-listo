package com.gamelisto.social.infrastructure.out.persistence.neo4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
@Component
@Profile("!test")
public class Neo4jConstraintInitializer implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(Neo4jConstraintInitializer.class);
  private final Neo4jClient neo4jClient;
  public Neo4jConstraintInitializer(Neo4jClient neo4jClient) {
    this.neo4jClient = neo4jClient;
  }
  @Override
  public void run(String... args) {
    try {
      neo4jClient.query(
          "CREATE CONSTRAINT IF NOT EXISTS FOR (u:User) REQUIRE u.id IS UNIQUE"
      ).run();
      log.info("Constraint de unicidad para :User(id) verificado/creado");
    } catch (Exception e) {
      log.warn("No se pudo crear el constraint de unicidad en Neo4j: {}", e.getMessage());
    }
  }
}