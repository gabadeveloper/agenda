package com.example.agenda.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Table(name = "ENDERECO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "NOME_RUA")
    private String nomeRua;

    @Column(name = "NUMERO_RUA")
    private Integer numeroRua;

    @Column(name = "CEP")
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$",
            message = "Formato de cep inválido. Utilize o formato: 12345-000")
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTATO")
    @JsonBackReference
    private Contato contato;
}
