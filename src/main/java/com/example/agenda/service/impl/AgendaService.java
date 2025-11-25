package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.dto.EnderecoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import com.example.agenda.service.IAgendaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map.Entry;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AgendaService implements IAgendaService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Override
    public ContatoDTO criarContato(ContatoDTO contatoDTO) {

        Optional<Contato> email = contatoRepository.findByEmail(contatoDTO.getEmail());

        if(email.isPresent()){
            return null;
        }
        else{
            List<Endereco> listaEnderecoVazia = new ArrayList<>();

            Contato contato = Contato.builder()
                    .nome(contatoDTO.getNome())
                    .email(contatoDTO.getEmail())
                    .telefone(contatoDTO.getTelefone())
                    .dataNascimento(contatoDTO.getDataNascimento())
                    .enderecoLista(listaEnderecoVazia)
                    .build();

            List<Endereco> enderecos = contatoDTO.getEnderecoLista().stream().map(endereco -> {
                return Endereco.builder()
                        .nomeRua(endereco.getNomeRua())
                        .numeroRua(endereco.getNumeroRua())
                        .cep(endereco.getCep())
                        .contato(contato)
                        .build();

            }).toList();
            listaEnderecoVazia.addAll(enderecos);

            Contato contatoSalvo = contatoRepository.save(contato);

            return ContatoDTO.builder()
                    .id(contatoSalvo.getId())
                    .nome(contatoSalvo.getNome())
                    .telefone(contatoSalvo.getTelefone())
                    .dataNascimento(contatoSalvo.getDataNascimento())
                    .enderecoLista(contatoSalvo.getEnderecoLista())
                    .build();
        }
    }

    @Override
    public List<ContatoDTO> buscarContatos() {

        List<ContatoDTO> listaContatosDTO = contatoRepository.findAll().stream().map(contato ->{
            return ContatoDTO.builder()
                    .id(contato.getId())
                    .nome(contato.getNome())
                    .email(contato.getEmail())
                    .telefone(contato.getTelefone())
                    .dataNascimento(contato.getDataNascimento())
                    .enderecoLista(contato.getEnderecoLista())
                    .build();
        }).toList();

        if(listaContatosDTO.isEmpty()){
            return null;
        }
        else {
            return listaContatosDTO;
        }
    }

    @Override
    public ContatoDTO buscarContato(UUID id) {
        boolean contato = contatoRepository.existsById(id);

        if(contato){
            Contato dadosContato = contatoRepository.getReferenceById(id);
            return ContatoDTO.builder()
                    .nome(dadosContato.getNome())
                    .telefone(dadosContato.getTelefone())
                    .enderecoLista(dadosContato.getEnderecoLista())
                    .build();
        }
        else{
            return null;
        }
    }

    @Override
    public ContatoDTO atualizarContato(UUID id, ContatoDTO contatoDTO) {

        boolean verificarContato = contatoRepository.existsById(id);

        if(!verificarContato){
            return null;
        }

        else{
            Contato contato = contatoRepository.findById(id).orElse(null);

            if(contatoDTO.getEnderecoLista() != null){
                atualizarEnderecoLista(contato.getEnderecoLista(), contatoDTO.getEnderecoLista(), contato);
            }


            alterarDados(contatoDTO, contato);

            contatoRepository.save(contato);

            return ContatoDTO.builder()
                    .nome(contato.getNome())
                    .email(contato.getEmail())
                    .telefone(contato.getTelefone())
                    .dataNascimento(contato.getDataNascimento())
                    .enderecoLista(contato.getEnderecoLista())
                    .build();
        }

    }

    private static void alterarDados(Object origem, Object destino) {

        String[] camposNulos = atributosNulos(origem);

        BeanUtils.copyProperties(origem, destino, camposNulos);
    }

    private static String[] atributosNulos (Object origem) {

        BeanWrapper wrappedSource = new BeanWrapperImpl(origem);
        HashSet<String> camposNulos = new HashSet<>();

        for (PropertyDescriptor campo : wrappedSource.getPropertyDescriptors()) {
            Object valorCampo = wrappedSource.getPropertyValue(campo.getName());

            if (valorCampo == null) camposNulos.add(campo.getName());
        }
        String[] campos = new String[camposNulos.size()];
        return camposNulos.toArray(campos);
    }

    private void atualizarEnderecoLista(
            List<Endereco> listaDTO, // CORREÇÃO: Deve ser a lista de DTOs
            List<Endereco> listaEntidade,
            Contato contatoPai) // CORREÇÃO: Usar este argumento como o Contato Pai
    {

        // 1. Prepara as coleções para manipulação
        Set<UUID> idsManter = new HashSet<>();
        List<Endereco> novosEnderecos = new ArrayList<>();

        // Mapeia os endereços existentes por ID para busca rápida (Atualização)
        Map<UUID, Endereco> mapaExistentes = listaEntidade.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(Endereco::getId, Function.identity()));

        // 2. Itera sobre o DTO: Criação e Atualização
        for(Endereco enderecoDTO : listaDTO){
            if (enderecoDTO.getId() != null){
                // CASO 1: ATUALIZAÇÃO
                Endereco enderecoExistente = mapaExistentes.get(enderecoDTO.getId());

                if(enderecoExistente != null){
                    alterarDados(enderecoDTO, enderecoExistente);
                    idsManter.add(enderecoDTO.getId()); // Marca para manter
                }
            }
            else{
                // CASO 2: CRIAÇÃO
                Endereco novoEndereco = Endereco.builder()
                        .nomeRua(enderecoDTO.getNomeRua())
                        .numeroRua(enderecoDTO.getNumeroRua())
                        .cep(enderecoDTO.getCep())
                        .contato(contatoPai)
                        .build();
                novosEnderecos.add(novoEndereco);
            }
        }

        // 3. REMOÇÃO DE ÓRFÃOS (A Lógica Mais Segura)
        // Usamos removeIf na coleção gerenciada para deletar os órfãos.
        // O JPA monitora essa chamada e aciona o delete devido ao orphanRemoval=true.

        // Remove qualquer endereço que tenha um ID, mas cujo ID não está na lista 'idsManter'
        listaEntidade.removeIf(endereco ->
                endereco.getId() != null && !idsManter.contains(endereco.getId())
        );

        // 4. Integração: Adiciona os novos endereços
        listaEntidade.addAll(novosEnderecos);
    }

    @Override
    public List<Contato> deletarContato(UUID id) {
        boolean contato = contatoRepository.existsById(id);

        if(contato){
            contatoRepository.deleteById(id);
        }
        else{
            return null;
        }
        return contatoRepository.findAll();
    }


}
