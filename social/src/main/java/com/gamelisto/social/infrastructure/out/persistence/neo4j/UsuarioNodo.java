package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("User")
@Getter
@Setter
public class UsuarioNodo {

  @Id private String id;

  @Property("username")
  private String username;

  @Property("avatar")
  private String avatar;
}
