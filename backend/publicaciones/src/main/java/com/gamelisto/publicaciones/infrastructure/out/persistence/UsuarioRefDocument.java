package com.gamelisto.publicaciones.infrastructure.out.persistence;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "usuarios_ref")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRefDocument {

  @Id private UUID id;

  @Indexed(unique = true)
  private String username;

  private String avatar;
  private String discordUserId;
}
