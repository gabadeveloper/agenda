package com.example.agenda.controller;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.service.impl.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RestController("/")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/criar")
    public ResponseEntity<ContatoDTO> criarContato(@RequestBody ContatoDTO contatoDTO){

        ContatoDTO contatoSalvoDto = agendaService.criarContato(contatoDTO);
        if(contatoSalvoDto != null){
            return ResponseEntity.ok(contatoSalvoDto);
        }
        return new ResponseEntity<ContatoDTO>(HttpStatus.FOUND);

    }

    @GetMapping("/buscar")
    public List<Contato> dizerOi(){
        return agendaService.buscarContatos();
    }


    @GetMapping("/buscar-contato-especifico/{id}")
    public ContatoDTO buscarContatoEspecifico(@PathVariable UUID id){
        return agendaService.buscarContato(id);
    }
}
