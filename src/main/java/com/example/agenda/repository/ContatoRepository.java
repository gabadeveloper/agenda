package com.example.agenda.repository;

import com.example.agenda.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContatoRepository extends JpaRepository<Contato, UUID> {
    Optional<Contato> findByEmail(String email);
}
