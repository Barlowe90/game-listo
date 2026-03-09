package com.gamelisto.social.infrastructure.in.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRefResponse {
  private UUID id;
  private String username;
  private String avatar;
}
