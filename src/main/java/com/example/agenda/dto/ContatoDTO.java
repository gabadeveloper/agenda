package com.example.agenda.dto;

import com.example.agenda.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
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

    private String nome;

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O e-mail deve ser um formato válido")
    private String email;

    @Pattern(regexp = "\\(\\d{2}\\) 9\\d{4}-\\d{4}",
            message = "Formato de telefone inválido. Use (DD) 9XXXX-XXXX")
    private String telefone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotEmpty(message = "O campo não pode estar vazio. Necessário adicionar pelo menos um endereço.")
    private List<Endereco> enderecoLista;

}
