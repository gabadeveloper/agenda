package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.dto.EnderecoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import com.example.agenda.service.IAgendaService;
import com.example.agenda.utils.ContatoPatchMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class AgendaService implements IAgendaService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ContatoPatchMapper mapper;


    @Override
    public ContatoDTO criarContato(ContatoDTO contatoDTO) {

        Optional<Contato> verificarEmail = contatoRepository.findByEmail(contatoDTO.getEmail());

        if(verificarEmail.isPresent()){
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
                    .email(contatoSalvo.getEmail())
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

        if(!contato){
            return null;
        }
        else{

            Contato dadosContato = contatoRepository.getReferenceById(id);
            return ContatoDTO.builder()
                    .nome(dadosContato.getNome())
                    .email(dadosContato.getEmail())
                    .telefone(dadosContato.getTelefone())
                    .dataNascimento(dadosContato.getDataNascimento())
                    .enderecoLista(dadosContato.getEnderecoLista())
                    .build();
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
            Contato contato = contatoRepository.getReferenceById(id);

            // 1. Carrega a Entidade
            Contato contatoAtual = contatoRepository.findById(id)
                    .orElse(null);

            if (contatoAtual == null) {
                return null;
            }

            mapper.updateContatoFromDto(contatoDTO, contatoAtual);

            // 3. ATUALIZAÇÃO DINÂMICA DA LISTA DE ENDEREÇOS
            gerenciarEnderecos(contatoAtual, contatoDTO.getEnderecoLista());

            // 4. Salva (Persiste todas as alterações, incluindo as do MapStruct e da lista)
            contatoRepository.save(contatoAtual);


            contatoRepository.save(contato);

            return mapper.toContatoDTO(contatoAtual);
        }

    }


    private void gerenciarEnderecos(Contato contatoOriginal, List<Endereco> novosEnderecosDTO) {

        // Só processa se a lista foi enviada no DTO (diferente de nulo)
        if (novosEnderecosDTO != null) {

            // REMOÇÃO DINÂMICA: Limpa a lista existente. (JPA/Hibernate fará o DELETE)
            contatoOriginal.getEnderecoLista().clear();

            // ADIÇÃO DINÂMICA: Adiciona os novos (ou re-enviados)
            novosEnderecosDTO.forEach(enderecoDTO -> {

                Endereco enderecoEntity = mapper.toEnderecoEntity(enderecoDTO);

                // CRUCIAL: Vincula a chave estrangeira
                enderecoEntity.setContato(contatoOriginal);

                contatoOriginal.getEnderecoLista().add(enderecoEntity);
            });
        }
    }


    @Override
    public List<Contato> deletarContato(UUID id) {
        boolean contato = contatoRepository.existsById(id);

        if(!contato){
            return null;
        }
        else{
            contatoRepository.deleteById(id);
        }
        return contatoRepository.findAll();
    }


}
