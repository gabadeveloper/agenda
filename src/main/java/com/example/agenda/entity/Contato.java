package com.example.agenda.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "CONTATOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate(true)
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_CONTATO")
    private UUID id;

    @Column(name = "NOME_CONTATO")
    @NotBlank(message = "O campo nome não deve estar vazio.")
    private String nome;

    @Column(name = "EMAIL_CONTATO")
    @NotBlank(message = "O campo 'email' não deve estar vazio.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @Column(name = "TELEFONE_CONTATO")
    @Pattern(regexp = "^\\([1-9][1-9]\\)\\s9[0-9]{4}-[0-9]{4}$",
            message = "Formato de telefone inválido.")
    private String telefone;


    @Column(name = "DATA_DE_NASCIMENTO_CONTATO")
    @JsonFormat(pattern = "dd/MM/yyyy")
    //@DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;


    @OneToMany(mappedBy = "contato", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Endereco> enderecoLista;
}
