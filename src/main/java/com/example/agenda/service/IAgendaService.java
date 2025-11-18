package com.example.agenda.service;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;

import java.util.List;
import java.util.UUID;

public interface IAgendaService {

    ContatoDTO criarContato(ContatoDTO contatoDTO);

    List<Contato> buscarContatos();

    ContatoDTO buscarContato(UUID id);
}
