package br.com.sicredi.vote.repository;

import br.com.sicredi.vote.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VotingSessionRepository extends JpaRepository<VotingSession, UUID> {

    Optional<VotingSession> findByAgendaId(UUID agendaId);

    List<VotingSession> findAllByOrderByCreatedAtDesc();

}
