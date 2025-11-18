package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import com.example.agenda.service.IAgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                    .nome(contatoSalvo.getNome())
                    .telefone(contatoSalvo.getTelefone())
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


}
