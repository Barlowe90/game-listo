package com.gamelisto.social.infrastructure.in.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRefResponse {
  private String id;
  private String username;
  private String avatar;
}
