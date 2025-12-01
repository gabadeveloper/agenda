package com.example.agenda.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    private UUID id;

    private String nomeRua;

    private Integer numeroRua;

    private String cep;
}
