package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.EstadoPeticion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "peticiones_union")
@CompoundIndex(
    name = "uniq_publicacion_usuario",
    def = "{'publicacionId': 1, 'usuarioId': 1}",
    unique = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeticionUnionDocument {

  @Id private UUID id;

  @Indexed private UUID publicacionId;

  @Indexed private UUID usuarioId;

  private EstadoPeticion estadoPeticion;
}
