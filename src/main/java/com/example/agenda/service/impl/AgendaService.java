package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import com.example.agenda.service.IAgendaService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Transactional
    public ContatoDTO atualizarContato(UUID id, ContatoDTO contatoDTO) {

        boolean verificarContato = contatoRepository.existsById(id);

        if(!verificarContato){
            return null;
        }

        else{
            Contato contato = contatoRepository.findById(id).orElse(null);

            if (contatoDTO.getEnderecoLista() != null) {
                atualizarEnderecoLista(
                        contatoDTO.getEnderecoLista(),
                        contato.getEnderecoLista(),
                        contato
                );
            }

            alterarDados(contatoDTO, contato);

            contato = contatoRepository.save(contato);

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

        // Obtém a lista de campos que são nulos no DTO (Origem)
        String[] camposNulos = atributosNulos(origem);

        // Combina os campos nulos com os campos que devem ser sempre protegidos.
        // Usamos Stream para uma combinação eficiente.
        String[] camposIgnorar = Stream.of(
                        // Campos Nulos (para ignorar na atualização parcial)
                        camposNulos,
                        // Campos Críticos (para proteger a Entidade Gerenciada)
                        new String[]{"id", "enderecoLista"}
                )
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        // Copia as propriedades, ignorando todos os campos listados
        BeanUtils.copyProperties(origem, destino, camposIgnorar);
    }

    private static String[] atributosNulos (Object origem) {

        final BeanWrapper wrappedSource = new BeanWrapperImpl(origem);
        HashSet<String> camposNulos = new HashSet<>();

        for (PropertyDescriptor campo : wrappedSource.getPropertyDescriptors()) {
            if (wrappedSource.isReadableProperty(campo.getName())) {
                Object valorCampo = wrappedSource.getPropertyValue(campo.getName());

                if (valorCampo == null) {
                    camposNulos.add(campo.getName());
                }
            }
        }
        // Retorna um array com os nomes dos campos nulos
        return camposNulos.toArray(new String[0]);
    }

    private void atualizarEnderecoLista(List<Endereco> listaDTO, List<Endereco> listaEntidade, Contato contato){
        Set<UUID> idsManter = new HashSet<>();
        List<Endereco> novosEnderecos = new ArrayList<>();

        Map<UUID, Endereco> mapasExistentes = listaEntidade.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(Endereco::getId, Function.identity()));

        for(Endereco enderecoDTO : listaDTO){
            if(enderecoDTO.getId() != null){
                Endereco enderecoExistente = mapasExistentes.get(enderecoDTO.getId());
                if(enderecoExistente != null){
                    alterarDados(enderecoDTO, enderecoExistente);
                    idsManter.add(enderecoDTO.getId());
                }
            }
            else{
                Endereco novoEndereco = Endereco.builder()
                        .nomeRua(enderecoDTO.getNomeRua())
                        .numeroRua(enderecoDTO.getNumeroRua())
                        .cep(enderecoDTO.getCep())
                        .contato(contato)
                        .build();
                novosEnderecos.add(novoEndereco);
            }
        }

        listaEntidade.removeIf(endereco -> endereco.getId() != null && !idsManter.contains(endereco.getId()));

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
