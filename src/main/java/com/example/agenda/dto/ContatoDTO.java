package com.example.agenda.dto;

import com.example.agenda.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContatoDTO {

    private UUID id;

    @NotBlank(message = "O campo nome não deve estar vazio.")
    private String nome;

    @NotBlank(message = "O campo 'email' não deve estar vazio.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @Pattern(regexp = "^\\([1-9][1-9]\\)\\s9[0-9]{4}-[0-9]{4}$",
            message = "Formato de telefone inválido.")
    private String telefone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    //@DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    private List<Endereco> enderecoLista;

}
