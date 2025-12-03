package com.example.agenda.controller;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.service.impl.AgendaService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RestController("/")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/criar")
    public ResponseEntity<ContatoDTO> criarContato(@Valid @RequestBody ContatoDTO contatoDTO){

        ContatoDTO contatoSalvoDto = agendaService.criarContato(contatoDTO);
        if(contatoSalvoDto != null){
            return new ResponseEntity<>(contatoSalvoDto, HttpStatus.CREATED);
        }
        return new ResponseEntity<ContatoDTO>(HttpStatus.FOUND);

    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ContatoDTO>> buscarContatosNaLista(){

        List<ContatoDTO> verificarListaDeContatos = agendaService.buscarContatos();

        if(verificarListaDeContatos == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<ContatoDTO>>(verificarListaDeContatos, HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ContatoDTO> buscarContatoEspecifico(@PathVariable UUID id){
        ContatoDTO verificarExistenciaContato = agendaService.buscarContato(id);

        if(verificarExistenciaContato == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(verificarExistenciaContato, HttpStatus.OK);
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<ContatoDTO> atualizarContato(@Valid @PathVariable UUID id, @RequestBody ContatoDTO contatoDTO){
        ContatoDTO verificar = agendaService.atualizarContato(id, contatoDTO);
        if(verificar == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(verificar, HttpStatus.OK);
        }
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<List<Contato>> deletarContato(@PathVariable UUID id){
        List<Contato> verificarContato = agendaService.deletarContato(id);
        if(verificarContato ==  null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(verificarContato, HttpStatus.NO_CONTENT);
        }


    }
}
