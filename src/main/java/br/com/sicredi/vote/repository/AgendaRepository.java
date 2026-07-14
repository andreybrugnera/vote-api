package br.com.sicredi.vote.repository;

import br.com.sicredi.vote.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgendaRepository extends JpaRepository<Agenda, UUID> {
}
