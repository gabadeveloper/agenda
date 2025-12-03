package com.example.agenda.controller;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.service.impl.AgendaService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RestController("/agenda-contato")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/criar")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um contato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "contato criado",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Contato.class))}),
            @ApiResponse(responseCode = "302", description = "contato existente encontrado",
                    content = @Content)})
    public ResponseEntity<ContatoDTO> criarContato(@Valid @RequestBody ContatoDTO contatoDTO){

        ContatoDTO contatoSalvoDto = agendaService.criarContato(contatoDTO);
        if(contatoSalvoDto != null){
            return new ResponseEntity<>(contatoSalvoDto, HttpStatus.CREATED);
        }
        return new ResponseEntity<ContatoDTO>(HttpStatus.FOUND);

    }




    @GetMapping("/buscar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buscar todos os contatos presentes na lista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "lista de contatos atual",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Contato.class))}),
            @ApiResponse(responseCode = "404", description = "lista de contatos inexistente",
                    content = @Content)})
    public ResponseEntity<List<ContatoDTO>> buscarContatosNaLista(){

        List<ContatoDTO> verificarListaDeContatos = agendaService.buscarContatos();

        if(verificarListaDeContatos == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<ContatoDTO>>(verificarListaDeContatos, HttpStatus.OK);
    }




    @GetMapping("/buscar/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buscar contato específico utilizando ID único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "contato encontrado",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Contato.class))}),
            @ApiResponse(responseCode = "404", description = "contato inexistente",
                    content = @Content)})
    public ResponseEntity<ContatoDTO> buscarContatoEspecifico(@PathVariable UUID id){
        ContatoDTO verificarExistenciaContato = agendaService.buscarContato(id);

        if(verificarExistenciaContato == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(verificarExistenciaContato, HttpStatus.OK);
    }




    @PatchMapping("/atualizar/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualizar apenas " +
            "informações desejadas de um contato utilizando ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "contato atualizado",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Contato.class))}),
            @ApiResponse(responseCode = "404", description = "contato não atualizado",
                    content = @Content)})
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar contato específico utilizando ID único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "contato excluído com sucesso",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Contato.class))}),
            @ApiResponse(responseCode = "404", description = "contato inexistente",
                    content = @Content)})
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
