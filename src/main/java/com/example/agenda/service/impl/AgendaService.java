package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.dto.EnderecoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import com.example.agenda.service.IAgendaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map.Entry;
import java.util.*;

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
            List<Endereco> teste = new ArrayList<>();

            Contato contato = Contato.builder()
                    .nome(contatoDTO.getNome())
                    .email(contatoDTO.getEmail())
                    .telefone(contatoDTO.getTelefone())
                    .dataNascimento(contatoDTO.getDataNascimento())
                    .enderecoLista(teste)
                    .build();

            List<Endereco> enderecos = contatoDTO.getEnderecoLista().stream().map(endereco -> {
                return Endereco.builder()
                        .nomeRua(endereco.getNomeRua())
                        .numeroRua(endereco.getNumeroRua())
                        .cep(endereco.getCep())
                        .contato(contato)
                        .build();

            }).toList();

            teste.addAll(enderecos);

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
    public List<Contato> buscarContatos() {
        return contatoRepository.findAll();
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
            Contato contato = contatoRepository.getReferenceById(id);

            contatoRepository.save(contato);

            return ContatoDTO.builder()
                    .nome(contato.getNome())
                    .email(contato.getEmail())
                    .telefone(contato.getTelefone())
                    .dataNascimento(contato.getDataNascimento())
                    .build();
        }

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
