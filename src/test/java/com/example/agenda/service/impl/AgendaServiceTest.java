package com.example.agenda.service.impl;

import com.example.agenda.dto.ContatoDTO;
import com.example.agenda.entity.Contato;
import com.example.agenda.entity.Endereco;
import com.example.agenda.repository.ContatoRepository;
import com.example.agenda.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @InjectMocks
    private AgendaService agendaService;

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    private ContatoDTO contatoDTO;
    private Contato contato;
    private UUID id;;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        endereco = Endereco.builder()
                .nomeRua("Rua A")
                .numeroRua(123)
                .cep("70000-000")
                .build();

        contatoDTO = ContatoDTO.builder()
                .nome("Bruno")
                .email("bruno@gmail.com")
                .telefone("(61) 98945-8965")
                .dataNascimento(LocalDate.of(2000, 1, 1))
                .enderecoLista(List.of(endereco))
                .build();

        contato = Contato.builder()
                .id(id)
                .nome("Bruno")
                .email("bruno@gmail.com")
                .telefone("(61) 98945-8965")
                .dataNascimento(LocalDate.of(2000, 1, 1))
                .enderecoLista(new ArrayList<>())
                .build();

        endereco = Endereco.builder()
                .nomeRua(endereco.getNomeRua())
                .numeroRua(endereco.getNumeroRua())
                .cep(endereco.getCep())
                .contato(contato)
                .build();
    }

    //==================== criarContato ====================
    @Test
    void criarContato_EmailExistente_RetornaNull() {
        when(contatoRepository.findByEmail(contatoDTO.getEmail()))
                .thenReturn(Optional.of(contato));

        ContatoDTO resultado = agendaService.criarContato(contatoDTO);

        assertNull(resultado);
        verify(contatoRepository, never()).save(any());
    }

    @Test
    void criarContato_EmailNaoExistente_ComEndereco_SalvaERetornaDTO() {
        when(contatoRepository.findByEmail(contatoDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(contatoRepository.save(any(Contato.class))).thenAnswer(i -> i.getArgument(0));

        ContatoDTO resultado = agendaService.criarContato(contatoDTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.getEnderecoLista().size());
        assertEquals("Rua A", resultado.getEnderecoLista().get(0).getNomeRua());
        verify(contatoRepository).save(any(Contato.class));
    }

    @Test
    void criarContato_EmailNaoExistente_SemEndereco_SalvaERetornaDTO() {
        contatoDTO.setEnderecoLista(List.of());
        when(contatoRepository.findByEmail(contatoDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(contatoRepository.save(any(Contato.class))).thenAnswer(i -> i.getArgument(0));

        ContatoDTO resultado = agendaService.criarContato(contatoDTO);

        assertNotNull(resultado);
        assertEquals(0, resultado.getEnderecoLista().size());
    }

    //==================== buscarContatos ====================
    @Test
    void buscarContatos_ListaVazia_RetornaNull() {
        when(contatoRepository.findAll()).thenReturn(List.of());

        List<ContatoDTO> resultado = agendaService.buscarContatos();

        assertNull(resultado);
    }

    @Test
    void buscarContatos_ListaComEnderecos_RetornaDTOs() {
        contato.getEnderecoLista().add(endereco);
        when(contatoRepository.findAll()).thenReturn(List.of(contato));

        List<ContatoDTO> resultado = agendaService.buscarContatos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getEnderecoLista().size());
    }

    //==================== buscarContato ====================
    @Test
    void buscarContato_ContatoExistente_RetornaDTOComEndereco() {
        contato.getEnderecoLista().add(endereco);
        when(contatoRepository.existsById(id)).thenReturn(true);
        when(contatoRepository.getReferenceById(id)).thenReturn(contato);

        ContatoDTO resultado = agendaService.buscarContato(id);

        assertNotNull(resultado);
        assertEquals(1, resultado.getEnderecoLista().size());
        assertEquals("Rua A", resultado.getEnderecoLista().get(0).getNomeRua());
    }

    @Test
    void buscarContato_ContatoInexistente_RetornaNull() {
        when(contatoRepository.existsById(id)).thenReturn(false);

        ContatoDTO resultado = agendaService.buscarContato(id);

        assertNull(resultado);
    }

    //==================== atualizarContato ====================
    @Test
    void atualizarContato_ContatoInexistente_RetornaNull() {
        when(contatoRepository.existsById(id)).thenReturn(false);

        ContatoDTO resultado = agendaService.atualizarContato(id, contatoDTO);

        assertNull(resultado);
        verify(contatoRepository, never()).save(any());
    }

    @Test
    void atualizarContato_ContatoExistente_ComEndereco_AtualizaERetornaDTO() {
        when(contatoRepository.existsById(id)).thenReturn(true);
        when(contatoRepository.findById(id)).thenReturn(Optional.of(contato));
        when(contatoRepository.save(any(Contato.class))).thenAnswer(i -> i.getArgument(0));

        ContatoDTO resultado = agendaService.atualizarContato(id, contatoDTO);

        assertNotNull(resultado);
        assertEquals("Bruno", resultado.getNome());
        assertEquals(contatoDTO.getEnderecoLista().size(), resultado.getEnderecoLista().size());
        verify(contatoRepository).save(contato);
    }

    //==================== deletarContato ====================
    @Test
    void deletarContato_ContatoInexistente_RetornaNull() {
        when(contatoRepository.existsById(id)).thenReturn(false);

        List<Contato> resultado = agendaService.deletarContato(id);

        assertNull(resultado);
        verify(contatoRepository, never()).deleteById(any());
    }

    @Test
    void deletarContato_ContatoExistente_DeletaERetornaLista() {
        when(contatoRepository.existsById(id)).thenReturn(true);
        when(contatoRepository.findAll()).thenReturn(List.of());

        List<Contato> resultado = agendaService.deletarContato(id);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(contatoRepository).deleteById(id);
    }
}
