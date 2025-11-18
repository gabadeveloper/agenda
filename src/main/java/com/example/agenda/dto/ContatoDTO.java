package com.example.agenda.dto;

import com.example.agenda.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContatoDTO {

    //private UUID id;

    private String nome;

    private String email;

    private String telefone;

    private List<Endereco> enderecoLista;

}
