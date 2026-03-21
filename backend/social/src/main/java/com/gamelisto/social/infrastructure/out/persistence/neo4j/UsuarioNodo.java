package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Node("User")
@Getter
@Setter
public class UsuarioNodo {

  @Id private UUID id;

  @Property("username")
  private String username;

  @Property("avatar")
  private String avatar;

  @Property("discordUserId")
  private String discordUserId;

  @Property("discordUsername")
  private String discordUsername;
}
