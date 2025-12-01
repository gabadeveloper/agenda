package com.example.agenda.utils;

import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;
import java.util.*;

public class BeanCopyUtils {

    public static void copiarPropriedadesNaoNulas(Object dtoInserido, Object entidadeSalva, String... extraIgnorar) {
        String[] propriedadesNulas = obterPropriedadesNulas(dtoInserido);

        // Combina as propriedades nulas com as propriedades extras a serem ignoradas
        List<String> combinedIgnores = new ArrayList<>(Arrays.asList(propriedadesNulas));
        combinedIgnores.addAll(Arrays.asList(extraIgnorar));

        // Agora, ele ignora nulos E quaisquer campos extras que você passar
        BeanUtils.copyProperties(dtoInserido, entidadeSalva, combinedIgnores.toArray(new String[0]));
    }

    private static String[] obterPropriedadesNulas(Object dtoInserido){
        final BeanWrapper dto = new BeanWrapperImpl(dtoInserido);
        PropertyDescriptor[] campos = dto.getPropertyDescriptors();

        Set<String> nomeCampos = new HashSet<>();
        for(PropertyDescriptor campo : campos){
            Object valorDto = dto.getPropertyValue(campo.getName());

            if (valorDto == null){
                nomeCampos.add(campo.getName());
            }
        }

        String[] camposNulos = new String[nomeCampos.size()];
        return nomeCampos.toArray(camposNulos);
    }

    public static void atualizarOuAdicionarEnderecos(Contato contatoExistente, List<Endereco> enderecosDTO) {

        if (enderecosDTO == null || enderecosDTO.isEmpty()) {
            return;
        }

        for (Endereco enderecoDTO : enderecosDTO) {
            if (enderecoDTO.getId() != null) {

                Optional<Endereco> enderecoExistenteOpt = contatoExistente.getEnderecoLista().stream()
                        .filter(e -> enderecoDTO.getId().equals(e.getId()))
                        .findFirst();

                if (enderecoExistenteOpt.isPresent()) {
                    Endereco enderecoExistente = enderecoExistenteOpt.get();
                    copiarPropriedadesNaoNulas(enderecoDTO, enderecoExistente);

                } else {
                    adicionarNovoEndereco(contatoExistente, enderecoDTO);
                }

            }
            else{
                adicionarNovoEndereco(contatoExistente, enderecoDTO);
            }
        }
    }

    private static void adicionarNovoEndereco(Contato contato, Endereco novoEndereco) {
        if (contato.getEnderecoLista() == null) {
            contato.setEnderecoLista(new java.util.ArrayList<>());
        }
        novoEndereco.setContato(contato);

        contato.getEnderecoLista().add(novoEndereco);
    }
}
