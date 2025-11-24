package com.example.agenda.utils;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.dto.EnderecoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ContatoPatchMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enderecoLista", ignore = true) // Trataremos a lista separadamente
    void updateContatoFromDto(ContatoDTO contatoPatchDTO, @MappingTarget Contato target);

    // Mapeamentos de Coleção e Retorno
    Endereco toEnderecoEntity(Endereco dto);
    ContatoDTO toContatoDTO(Contato entity);
}
